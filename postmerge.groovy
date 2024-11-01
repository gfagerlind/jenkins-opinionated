// here is the postmerge job code
[
    init: {
        /// -----------------------------------------------------------------
        // GOCD style WIP limit per stage, with inverse priority (or not)
        // AND dynamic wip limit.
        stageWithWip = import_file('libs/StageWithWip.groovy')
    },
    main: {
        stageWithWip(name: "c", wip: 4) {
            try {
                echo "a"
                sleep(30 - (currentBuild.number - 374) * 5)
                echo "a"
                sleep(30 - (currentBuild.number - 374) * 5)
                echo "a"
                sleep(30 - (currentBuild.number - 374) * 5)
                echo "done"
            } catch (Exception e) {
                echo 'here aborted'
                echo "${e}"
                throw e
            }
        }
        stageWithWip(name: "b", wip: 2) {
            echo "b"
            sleep 10
        }
    }
]
