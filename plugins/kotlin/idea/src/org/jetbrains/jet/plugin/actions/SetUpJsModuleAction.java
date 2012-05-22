/*
 * Copyright 2010-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.plugin.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.plugin.k2jsrun.K2JSRunnerUtils;
import org.jetbrains.jet.plugin.project.JsModuleDetector;
import org.jetbrains.jet.utils.PathUtil;

import java.io.File;
import java.io.IOException;

import static org.jetbrains.jet.plugin.k2jsrun.K2JSRunnerUtils.copyFileToDir;

/**
 * @author Pavel Talanov
 */
public final class SetUpJsModuleAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            notifyFailure("Internal error: Project not found.");
            return;
        }

        File jsLibPath = PathUtil.getDefaultJsLibPath();
        if (jsLibPath == null) {
            notifyFailure("JavaScript library not found. Make sure plugin is installed properly.");
            return;
        }

        File rootDir = getRootDir(project);
        if (!rootDir.isDirectory()) {
            notifyFailure("Internal error: Broken content root.");
            return;
        }

        if (!copyJsLib(jsLibPath, rootDir)) return;

        File file = new File(rootDir, JsModuleDetector.INDICATION_FILE_NAME);
        if (file.exists()) {
            notifyInfo("File " + file.getName() + " already exists.");
            return;
        }

        createIndicationFile(file);

        refreshRootDir(project);
    }

    private static void refreshRootDir(@NotNull Project project) {
        getContentRoot(project).refresh(true, false);
    }

    private static void createIndicationFile(@NotNull File file) {
        try {
            FileUtil.writeToFile(file, PathUtil.JS_LIB_JAR_NAME);
        }
        catch (IOException e) {
            notifyFailure("Failed to write file " + file.getName());
        }
    }

    private static boolean copyJsLib(@NotNull File jsLibPath, @NotNull File rootDir) {
        try {
            copyFileToDir(jsLibPath, rootDir);
        }
        catch (IOException e) {
            notifyFailure("Failed to copy file: " + e.getMessage());
            return false;
        }
        return true;
    }

    @NotNull
    private static File getRootDir(@NotNull Project project) {
        VirtualFile contentRoot = getContentRoot(project);
        return new File(contentRoot.getPath());
    }

    @NotNull
    private static VirtualFile getContentRoot(@NotNull Project project) {
        Module module = K2JSRunnerUtils.getJsModule(project);
        return ModuleRootManager.getInstance(module).getContentRoots()[0];
    }

    public static void notifyFailure(@NotNull String message) {
        Notifications.Bus.notify(new Notification("Set Up Kotlin to JavaScript Module", "Fail",
                                                  message,
                                                  NotificationType.ERROR));
    }

    public static void notifyInfo(@NotNull String message) {
        Notifications.Bus.notify(new Notification("Set Up Kotlin to JavaScript Module", "Information",
                                                  message,
                                                  NotificationType.INFORMATION));
    }
}
