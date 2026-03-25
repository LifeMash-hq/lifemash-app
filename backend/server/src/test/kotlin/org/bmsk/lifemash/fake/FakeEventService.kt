package org.bmsk.lifemash.fake

import org.bmsk.lifemash.event.EventService
import org.bmsk.lifemash.event.EventServiceImpl

fun fakeEventService(): EventService =
    EventServiceImpl(FakeEventRepository(), FakeGroupRepository(), FakeFcmService())
