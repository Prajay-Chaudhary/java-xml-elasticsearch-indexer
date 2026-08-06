package com.adelean.assessment;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class XmlUnzipper {
  public static void unzipToDirectory(Path zipPath, Path outputDir) throws IOException {
    try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (entry.isDirectory())
          continue;
        Path outFile = outputDir.resolve(entry.getName()).normalize();
        Files.createDirectories(outFile.getParent());
        try (OutputStream os = Files.newOutputStream(outFile)) {
          byte[] buffer = new byte[8192];
          int len;
          while ((len = zis.read(buffer)) > 0) {
            os.write(buffer, 0, len);
          }
        }
        zis.closeEntry();
      }
    }
  }
}
