package com.sarencurrie.lois

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class Handler : RequestHandler<Any, String?> {
    override fun handleRequest(unused: Any, unused2: Context?): String {
        checkLocations()
        return "200 OK"
    }
}