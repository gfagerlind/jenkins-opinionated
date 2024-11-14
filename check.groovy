// For completeness, add a check job as well
[
    init: {
        import_file('libs/TypicalJob.groovy')
        import_file('libs/NodeWithPriority.groovy')
        import_file('libs/Reg.groovy')
    },
    main: {
        runScope('check')
    }
]
