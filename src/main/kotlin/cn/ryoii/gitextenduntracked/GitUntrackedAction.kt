package cn.ryoii.gitextenduntracked

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vcs.actions.VcsContextUtil

class GitUntrackedAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let {
            ProgressManager.getInstance().run(GitUntrackedTask(it, VcsContextUtil.selectedFilePaths(event.dataContext)))
        }
    }
}