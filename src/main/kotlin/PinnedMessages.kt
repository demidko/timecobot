import co.touchlab.stately.isolate.IsolateState

private typealias LastEpochSecond = Long

private typealias MessageId = Long

private typealias ChatId = Long

typealias PinnedMessages = IsolateState<MutableMap<ChatId, MutableMap<MessageId, LastEpochSecond>>>