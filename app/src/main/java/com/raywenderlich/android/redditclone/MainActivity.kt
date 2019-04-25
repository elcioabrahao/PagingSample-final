/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.redditclone


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager

import com.raywenderlich.android.redditclone.database.RedditDb
import com.raywenderlich.android.redditclone.networking.RedditPost
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private val adapter = RedditAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initializeList()
  }

  private fun initializeList() {
    list.layoutManager = LinearLayoutManager(this)
    list.adapter = adapter

    //1
    val config = PagedList.Config.Builder()
        .setPageSize(30)
        .setEnablePlaceholders(false)
        .build()

    //2
    val liveData = initializedPagedListBuilder(config).build()

    //3
    liveData.observe(this, Observer<PagedList<RedditPost>> { pagedList ->
      adapter.submitList(pagedList)
    })
  }

  private fun initializedPagedListBuilder(config: PagedList.Config):
          LivePagedListBuilder<Int, RedditPost> {

    val database = RedditDb.create(this)
    val livePageListBuilder = LivePagedListBuilder<Int, RedditPost>(
        database.postDao().posts(),
        config)
    livePageListBuilder.setBoundaryCallback(RedditBoundaryCallback(database))
    return livePageListBuilder
  }
}
