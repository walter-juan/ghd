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
uri = URI("https://api.github.com/repos/Remix-Design/RemixIcon/releases/latest")
res = Net::HTTP.get_response(uri)
raise "HTTP Error" unless res.is_a?(Net::HTTPSuccess)

result = JSON.parse(res.body)
zipball_url = result['zipball_url']

destination = "icons/remixicon/src/main/resources/download.zip"
download_file(zipball_url, destination)

file_path   = "icons/remixicon/src/main/resources/download.zip"
destination = "icons/remixicon/src/main/resources/download/"
extract_zip(file_path, destination)

# ###########################################################
# # Rename the icons
# ###########################################################
downloadEntries = Dir.entries("icons/remixicon/src/main/resources/download/").select {|f| !f.start_with?('.')}
raise "More than one directory" if downloadEntries.size > 1
File.rename "icons/remixicon/src/main/resources/download/#{downloadEntries.first}", 'icons/remixicon/src/main/resources/download/remixicon'

FileUtils.remove_dir("icons/remixicon/src/main/resources/remixicon", true)

Dir.chdir("icons/remixicon/src/main/resources/download/remixicon/icons") {
    Dir.entries('.').select {|f| !f.start_with?('.')}.each { |f|
      name = f.downcase
        .gsub(/&/, '_and_') # folder special case
        .gsub(/\W/, '_')    # clean non word characters
        .gsub('-', '_')
        .gsub(' ', '_')
        .gsub(/_(_)+/, '_')
      File.rename f, name
    }
}

Dir.entries("icons/remixicon/src/main/resources/download/remixicon/icons").select {|f| !f.start_with?('.')}.each { |f|
  Dir.chdir("icons/remixicon/src/main/resources/download/remixicon/icons/#{f}") {
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
# Move icons to final destination and clean-up
###########################################################
FileUtils.mv("icons/remixicon/src/main/resources/download/remixicon/icons", "icons/remixicon/src/main/resources")
FileUtils.mv("icons/remixicon/src/main/resources/icons", "icons/remixicon/src/main/resources/remixicon")

File.delete("icons/remixicon/src/main/resources/download.zip")
FileUtils.remove_dir("icons/remixicon/src/main/resources/download", true)

###########################################################
# Create the Kotlin files
###########################################################
Dir.chdir("icons/remixicon/src/main/") {
  res_file = File.new("kotlin/com/woowla/compose/remixicon/RemixiconRes.kt", "w")
  res_file.puts("package com.woowla.compose.remixicon")
  res_file.puts("")
  res_file.puts("object RemixiconRes")
  res_file.puts("")

  painter_file = File.new("kotlin/com/woowla/compose/remixicon/RemixiconPainter.kt", "w")
  painter_file.puts("package com.woowla.compose.remixicon")
  painter_file.puts("")
  painter_file.puts("import androidx.compose.runtime.Composable")
  painter_file.puts("import androidx.compose.ui.graphics.painter.Painter")
  painter_file.puts("import androidx.compose.ui.res.painterResource")
  painter_file.puts("")
  painter_file.puts("object RemixiconPainter")
  painter_file.puts("")

  Dir.entries("resources/remixicon").select {|f| !f.start_with?('.')}.each { |dirname|
    Dir.entries("resources/remixicon/#{dirname}").select {|f| !f.start_with?('.')}.each { |filename|
      next unless filename.end_with?('.svg')
      dirnameCamelCase = dirname.split('_').collect(&:capitalize).join
      filenameCamelCase = filename.split('_').collect(&:capitalize).join.split('.').first
      res_file.puts("public val RemixiconRes.#{dirnameCamelCase}#{filenameCamelCase}: String")
      res_file.puts("    get() = \"remixicon/#{dirname}/#{filename}\"")
      res_file.puts("")
      painter_file.puts("public val RemixiconPainter.#{dirnameCamelCase}#{filenameCamelCase}: Painter")
      painter_file.puts("    @Composable")
      painter_file.puts("    get() = painterResource(RemixiconRes.#{dirnameCamelCase}#{filenameCamelCase})")
      painter_file.puts("")
    }
  }

  res_file.close
  painter_file.close
}