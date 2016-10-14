package org.pcsoft.plugin.intellij.asn1.template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Christoph on 13.10.2016.
 */
public class Asn1TemplateFileAction extends AnAction {
    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        final VirtualFile virtualFile = e.getData(DataKeys.VIRTUAL_FILE);
        final Project project = e.getData(DataKeys.PROJECT);
        if (project == null || virtualFile == null) {
            e.getPresentation().setVisible(false);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile virtualFile = e.getData(DataKeys.VIRTUAL_FILE).isDirectory() ? e.getData(DataKeys.VIRTUAL_FILE) : e.getData(DataKeys.VIRTUAL_FILE).getParent();

        final String moduleName = JOptionPane.showInputDialog(e.getInputEvent().getComponent(), "Enter a module name for the ASN1 file:");
        if (moduleName == null)
            return;

        try {
            final VirtualFile templateFile = virtualFile.createChildData(null, moduleName + ".asn");

            try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                try (final Scanner scanner = new Scanner(getClass().getResourceAsStream("/templates/module.asn"))) {
                    while (scanner.hasNextLine()) {
                        final String nextLine = scanner.nextLine() + System.lineSeparator();
                        out.write(nextLine.replace("$NAME$", moduleName).getBytes("UTF-8"));
                    }
                }
                out.flush();

                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        templateFile.setBinaryContent(out.toByteArray());
                    } catch (IOException e1) {
                        throw new AssertionError("unable to write data", e1);
                    }
                });
            }
        } catch (IOException e1) {
            LoggerFactory.getLogger(Asn1TemplateFileAction.class).error("unable to create ASN1 file", e1);
            JOptionPane.showMessageDialog(null, "Unable to create ASN1 file: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
