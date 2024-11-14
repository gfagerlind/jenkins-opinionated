[
    init: {
        /// ------------------------------------------------------
        import_file('libs/DependentGate.groovy')
        import_file('libs/TypicalJob.groovy')
    },
    main: {
        dependentGate(4, {
            // TODO: the rebase activities
            def echohej = { echo "hej" }
            def fail = { node { sh "false" }}

            def jobs = [
                    run1: {
                        echo "sleep"
                        sleep(40 - (currentBuild.number - 97) * 5)
                    },
                    run2: echohej,
                    run3: echohej,
                    run4: echohej,
                    run5: echohej,
                    run6: echohej,
                    run7: echohej,
                    run8: echohej,
                    run9: echohej,
                    runa: { shJob('date')},
                    runb: {
                        sleep 30
                    },
                    //f: fail,
                    failFast: true
            ]

            // Todo generate jobs
            parallel(jobs)
        },
        {
            // TODO: the merge activities
        }
        )
    // ------------------------------------------------------------------
    }
]
