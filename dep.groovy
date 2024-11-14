[
    init: {
        /// ------------------------------------------------------
        import_file('libs/DependentGate.groovy')
        import_file('libs/TypicalJob.groovy')
        import_file('libs/Reg.groovy')
    },
    main: {
        dependentGate(4, {
            runScope('gate')
        },
        {
            // TODO: the merge activities
        }
        )
    // ------------------------------------------------------------------
    }
]
