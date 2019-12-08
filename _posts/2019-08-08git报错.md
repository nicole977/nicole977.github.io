不知道是不是太久没用git了，今天突然报错了（无奈脸╮(╯▽╰)╭）

git add .

git commit -m '备注改了什么地方或添加了什么内容'

git push

然后就出现以下错误：
```
 ! [rejected]        master -> master (fetch first)
error: failed to push some refs to 'http://git.dbnewyouth.com/dbnewyouth/StudyNotes.git'
hint: Updates were rejected because the remote contains work that you do
hint: not have locally. This is usually caused by another repository pushing
hint: to the same ref. You may want to first integrate the remote changes
hint: (e.g., 'git pull ...') before pushing again.
hint: See the 'Note about fast-forwards' in 'git push --help' for details.
```

网上说主要原因是github中的README.md文件不在本地代码目录中

可以通过如下命令进行代码合并【注：pull=fetch+merge]

git pull --rebase origin master

执行上面代码后可以看到本地代码库中多了README.md文件

此时再执行语句 git push -u origin master即可完成代码上传到github

可是我明明就没碰过README.md

算了能用就好╮(╯▽╰)╭

---

二更

emmmm...又出现问题了

这次出现的错误是
```
fatal: You are not currently on a branch.
To push the history leading to the current (detached HEAD)
state now, use

    git push origin HEAD:<name-of-remote-branch>
```

找到的解决办法是：

1. 查看所有变化的文件，把有改动的先删除。(单纯删除总觉得不保险, 注意, 注意, 注意, 这里最好是将自己的项目 copy 一份到本地其他地方作为备份)
`git status`
2. 切回master主分支
`git checkout master`
3. 更新最新的代码
`git pull`
4. 切回之前使用的dev分支
`git checkout dev`


GIT使用过程出现(master|REBASE 1/1)

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190808152907.PNG)

使用git rebase --abort 可以解决代码回退的问题

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190808155011.PNG)

