// here is the postmerge job code
[
    init: {
        /// -----------------------------------------------------------------
        // GOCD style WIP limit per stage, with inverse priority (or not)
        // AND dynamic wip limit.
        // TODO i think this is wrong
        stageWithWip = import_file('libs/StageWithWip.groovy')
        import_file('libs/Reg.groovy')
    },
    main: {
        stageWithWip(name: "postmerge", wip: 4) {
            try {
                runScope('postmerge')
            } catch (Exception e) {
                echo 'here aborted'
                echo "${e}"
                throw e
            }
        }
        stageWithWip(name: "post-post-merge", wip: 2) {
            echo "b"
            sleep 10
        }
    }
]
