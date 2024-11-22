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
        parallel (
            postpostmerge: {
                stageWithWip(name: "post-post-merge", wip: 2) {
                    runScope('post-post-merge')
                }
            },
            paralleltopostpostmerge: {
                stage('serial stage a') {
                    echo "Just to show that fan out works as well"
                }
                stage('serial stage b') {
                    echo "yeah, you can serial steps here, or fan out again"
                }
            },
            failFast: false
        )
        stage("post-post-post-merge") {
            echo "And fan in - but don't do that, its annoying!"
        }
    }
]
