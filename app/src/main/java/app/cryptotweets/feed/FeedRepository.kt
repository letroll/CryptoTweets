package app.cryptotweets.feed

import androidx.lifecycle.asFlow
import androidx.paging.toLiveData
import app.cryptotweets.Utils.FEED_LIST_COUNT
import app.cryptotweets.Utils.FEED_LIST_ID
import app.cryptotweets.Utils.FEED_LIST_TYPE
import app.cryptotweets.Utils.Resource.Companion.error
import app.cryptotweets.Utils.Resource.Companion.loading
import app.cryptotweets.Utils.Resource.Companion.success
import app.cryptotweets.feed.room.FeedDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val feedService: FeedService,
    private val feedDao: FeedDao
) {
    fun getFeed() = flow {
        emit(loading(null))
        try {
            val tweetsResponse = feedService.getTweets(
                listType = FEED_LIST_TYPE,
                listId = FEED_LIST_ID,
                count = FEED_LIST_COUNT
            )
            feedDao.insertAll(tweetsResponse)
            feedDao.getAll().toLiveData(10).asFlow().collect { results ->
                emit(success(results))
            }
        } catch (exception: Exception) {
            emit(error(exception.localizedMessage!!, null))
        }
    }
}