package org.pcsoft.plugin.intellij.asn1.language;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by pfeifchr on 27.09.2016.
 */
public class Asn1FileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(Asn1FileType.INSTANCE, "asn1");
    }
}