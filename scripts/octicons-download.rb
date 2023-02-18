require 'json'
require 'net/http'
require 'uri'
require 'open-uri'
require 'zip'
require 'fileutils'

def download_file(url, destination)
    download = URI.open(url)
    IO.copy_stream(download, destination)
end

def extract_zip(file, destination)
  FileUtils.mkdir_p(destination)
  Zip::File.open(file) do |zip_file|
    zip_file.each do |f|
      fpath = File.join(destination, f.name)
      zip_file.extract(f, fpath) unless File.exist?(fpath)
    end
  end
end

###########################################################
# Move to parent folder
###########################################################
Dir.chdir ".." if Dir.pwd.end_with?("/scripts")

###########################################################
# Download icons
###########################################################
uri = URI("https://api.github.com/repos/primer/octicons/releases/latest")
res = Net::HTTP.get_response(uri)
raise "HTTP Error" unless res.is_a?(Net::HTTPSuccess)

result = JSON.parse(res.body)
zipball_url = result['zipball_url']

destination = "icons/octicons/src/main/resources/download.zip"
download_file(zipball_url, destination)

file_path   = "icons/octicons/src/main/resources/download.zip"
destination = "icons/octicons/src/main/resources/download/"
extract_zip(file_path, destination)

###########################################################
# Rename the icons
###########################################################
downloadEntries = Dir.entries("icons/octicons/src/main/resources/download/").select {|f| !f.start_with?('.')}
raise "More than one directory" if downloadEntries.size > 1
File.rename "icons/octicons/src/main/resources/download/#{downloadEntries.first}", 'icons/octicons/src/main/resources/download/octicons'

FileUtils.remove_dir("icons/octicons/src/main/resources/octicons", true)

Dir.chdir("icons/octicons/src/main/resources/download/octicons/icons") {
    Dir.entries('.').select {|f| !f.start_with?('.')}.each { |f|
        if f.end_with?('-24.svg')
            name = f.downcase.gsub('-', '_').gsub(' ', '_').gsub('_24.svg', '.svg')
            File.rename f, name
        else
            File.delete(f)
        end
    }
}

###########################################################
# Move icons to final destination and clean-up
###########################################################
FileUtils.mv("icons/octicons/src/main/resources/download/octicons/icons", "icons/octicons/src/main/resources")
FileUtils.mv("icons/octicons/src/main/resources/icons", "icons/octicons/src/main/resources/octicons")

File.delete("icons/octicons/src/main/resources/download.zip")
FileUtils.remove_dir("icons/octicons/src/main/resources/download", true)

###########################################################
# Create the Kotlin files
###########################################################
Dir.chdir("icons/octicons/src/main/") {
  res_file = File.new("kotlin/com/woowla/compose/octoicons/OctoiconsRes.kt", "w")
  res_file.puts("package com.woowla.compose.octoicons")
  res_file.puts("")
  res_file.puts("object OctoiconsRes")
  res_file.puts("")

  painter_file = File.new("kotlin/com/woowla/compose/octoicons/OctoiconsPainter.kt", "w")
  painter_file.puts("package com.woowla.compose.octoicons")
  painter_file.puts("")
  painter_file.puts("import androidx.compose.runtime.Composable")
  painter_file.puts("import androidx.compose.ui.graphics.painter.Painter")
  painter_file.puts("import androidx.compose.ui.res.painterResource")
  painter_file.puts("")
  painter_file.puts("object OctoiconsPainter")
  painter_file.puts("")

  Dir.entries("resources/octicons").select {|f| !f.start_with?('.')}.each { |filename|
    next unless filename.end_with?('.svg')
    filenameCamelCase = filename.split('_').collect(&:capitalize).join.split('.').first
    res_file.puts("public val OctoiconsRes.#{filenameCamelCase}: String")
    res_file.puts("    get() = \"octicons/#{filename}\"")
    res_file.puts("")

    painter_file.puts("public val OctoiconsPainter.#{filenameCamelCase}: Painter")
    painter_file.puts("    @Composable")
    painter_file.puts("    get() = painterResource(OctoiconsRes.#{filenameCamelCase})")
    painter_file.puts("")
  }

  res_file.close
  painter_file.close
}