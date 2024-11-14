// here is the postmerge job code
[
    init: {
        /// -----------------------------------------------------------------
        // GOCD style WIP limit per stage, with inverse priority (or not)
        // AND dynamic wip limit.
        import_file('libs/Reg.groovy')
        import_file('libs/StageWithWip.groovy')
    },
    main: {
        // TODO: this could also be generated to avoid defining so much in jenkins/groovy
        // for example, in make, you could have post-post-merge depend on post-merge
        stageWithWip(name: "post-merge", wip: 4) {
            try {
                runScope('post-merge')
            } catch (Exception e) {
                echo 'here aborted'
                echo "${e}"
                throw e
            }
        }
        stageWithWip(name: "post-post-merge", wip: 2) {
            runScope('post-post-merge')
        }
    }
]
