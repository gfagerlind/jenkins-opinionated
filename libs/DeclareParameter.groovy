// declare parameters and assure they are consistent.
{ ->
    _parameters = []
    declareParameter = { p ->
        _parameters += p
    }
    withParameters = { cls ->
        cls()
        properties([
          parameters(_parameters)
        ])
    }
}
