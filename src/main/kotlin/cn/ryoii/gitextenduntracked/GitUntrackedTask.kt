package cn.ryoii.gitextenduntracked

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.util.Functions
import com.intellij.vcsUtil.VcsFileUtil
import com.intellij.vcsUtil.VcsUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitCommandResult
import git4idea.commands.GitLineHandler

class GitUntrackedTask(project: Project, private val selectedFiles: List<FilePath>)
    : Task.Modal(project, "Removing Files from VCS", true) {

    private val log = thisLogger()

    override fun run(indicator: ProgressIndicator) {

        VcsUtil.groupByRoots(project, selectedFiles, Functions.identity()).forEach { (root, paths) ->
            GitLineHandler(project, root.path, GitCommand.RM).apply {
                addParameters("--cache", "-r")
                addRelativePaths(paths)
            }.runGitLine()

            VcsFileUtil.markFilesDirty(project, paths)
        }
    }

    private fun GitLineHandler.runGitLine() {
        Git.getInstance().runCommand(this)
                .takeUnless(GitCommandResult::success)
                ?.let(GitCommandResult::getErrorOutput)
                ?.forEach(log::error)
    }
}