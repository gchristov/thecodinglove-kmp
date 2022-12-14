package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.commonservice.CommonServiceModule
import com.gchristov.thecodinglove.htmlparse.HtmlParseModule
import com.gchristov.thecodinglove.kmpcommondi.DiGraph
import com.gchristov.thecodinglove.kmpcommondi.KmpCommonDiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import com.gchristov.thecodinglove.kmpcommondi.registerModules
import com.gchristov.thecodinglove.kmpcommonfirebase.KmpCommonFirebaseModule
import com.gchristov.thecodinglove.kmpcommonnetwork.KmpCommonNetworkModule
import com.gchristov.thecodinglove.search.PreloadPubSubService
import com.gchristov.thecodinglove.search.SearchApiService
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slack.SlackModule
import com.gchristov.thecodinglove.slack.SlackSlashCommandApiService

fun main() {
    setupDi()
    setupServices()
}

private fun setupDi() {
    // Add all modules that should participate in dependency injection for this app
    DiGraph.registerModules(
        listOf(
            KmpCommonDiModule.module,
            KmpCommonFirebaseModule.module,
            KmpCommonNetworkModule.module,
            CommonServiceModule.module,
            HtmlParseModule.module,
            SearchModule.module,
            SearchDataModule.module,
            SlackModule.module,
        )
    )
}

private fun setupServices() {
    DiGraph.inject<SearchApiService>().register()
    DiGraph.inject<SlackSlashCommandApiService>().register()
    DiGraph.inject<PreloadPubSubService>().register()
}