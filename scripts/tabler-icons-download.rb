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
uri = URI("https://api.github.com/repos/tabler/tabler-icons/releases/latest")
res = Net::HTTP.get_response(uri)
raise "HTTP Error" unless res.is_a?(Net::HTTPSuccess)

result = JSON.parse(res.body)
zipball_url = result['zipball_url']

FileUtils.mkdir_p("icons/tabler/src/main/resources/download/")
destination = "icons/tabler/src/main/resources/download/zipball.zip"
download_file(zipball_url, destination)

file_path   = "icons/tabler/src/main/resources/download/zipball.zip"
destination = "icons/tabler/src/main/resources/download/zipball/"
extract_zip(file_path, destination)

###########################################################
# Move download icons
###########################################################
downloadEntries = Dir.entries("icons/tabler/src/main/resources/download/zipball/").select {|f| !f.start_with?('.')}
raise "More than one directory" if downloadEntries.size > 1

FileUtils.mv(
  "icons/tabler/src/main/resources/download/zipball/#{downloadEntries.first}/icons",
  "icons/tabler/src/main/resources/download/tabler",
)

###########################################################
# Rename the icons
###########################################################
FileUtils.remove_dir("icons/tabler/src/main/resources/tabler", true)

[
    "icons/tabler/src/main/resources/download/tabler/filled",
    "icons/tabler/src/main/resources/download/tabler/outline",
].each { |dir|
    Dir.chdir(dir) {
        Dir.entries('.').select {|f| !f.start_with?('.')}.each { |f|
          name = f.downcase
            .gsub('-', '_')
            .gsub(' ', '_')
            .gsub(/_(_)+/, '_')
          File.rename f, name
        }
    }
}

###########################################################
# Remove current files
###########################################################
Dir.chdir("icons/tabler/src/main/kotlin/com/woowla/compose/tabler") {
    Dir.entries('.').select {|f| !f.start_with?('.')}.each { |f|
        File.delete(f)
    }
}
FileUtils.remove_dir("icons/tabler/src/main/resources/tabler/filled", true)
FileUtils.remove_dir("icons/tabler/src/main/resources/tabler/outline", true)

###########################################################
# Move icons to final destination and clean-up
###########################################################
FileUtils.mv("icons/tabler/src/main/resources/download/tabler", "icons/tabler/src/main/resources")
FileUtils.remove_dir("icons/tabler/src/main/resources/download", true)

###########################################################
# Create the Kotlin files
###########################################################
Dir.chdir("icons/tabler/src/main/") {
  res_file = File.new("kotlin/com/woowla/compose/tabler/TablerIconsRes.kt", "w")
  res_file.puts("package com.woowla.compose.tabler")
  res_file.puts("")
  res_file.puts("object TablerIconsRes")
  res_file.puts("")

  painter_file = File.new("kotlin/com/woowla/compose/tabler/TablerIconsPainter.kt", "w")
  painter_file.puts("package com.woowla.compose.tabler")
  painter_file.puts("")
  painter_file.puts("import androidx.compose.runtime.Composable")
  painter_file.puts("import androidx.compose.ui.graphics.painter.Painter")
  painter_file.puts("import androidx.compose.ui.res.painterResource")
  painter_file.puts("")
  painter_file.puts("object TablerIconsPainter")
  painter_file.puts("")

  Dir.entries("resources/tabler").select {|f| !f.start_with?('.')}.each { |dirname|
    Dir.entries("resources/tabler/#{dirname}").select {|f| !f.start_with?('.')}.each { |filename|
      next unless filename.end_with?('.svg')
      dirnameCamelCase = dirname.split('_').collect(&:capitalize).join
      filenameCamelCase = filename.split('_').collect(&:capitalize).join.split('.').first
      res_file.puts("public val TablerIconsRes.#{dirnameCamelCase}#{filenameCamelCase}: String")
      res_file.puts("    get() = \"tabler/#{dirname}/#{filename}\"")
      res_file.puts("")
      painter_file.puts("public val TablerIconsPainter.#{dirnameCamelCase}#{filenameCamelCase}: Painter")
      painter_file.puts("    @Composable")
      painter_file.puts("    get() = painterResource(TablerIconsRes.#{dirnameCamelCase}#{filenameCamelCase})")
      painter_file.puts("")
    }
  }

  res_file.close
  painter_file.close
}