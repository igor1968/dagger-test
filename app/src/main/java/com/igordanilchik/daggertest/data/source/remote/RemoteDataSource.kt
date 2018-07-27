package com.igordanilchik.daggertest.data.source.remote

import com.igordanilchik.daggertest.api.ClientApi
import com.igordanilchik.daggertest.dto.inner.Catalogue
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import rx.Observable

/**
 * @author Igor Danilchik
 */
class RemoteDataSource(httpClient: OkHttpClient): IRemoteDataSource {

    private val restApi: ClientApi =
            Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .baseUrl(ClientApi.API_BASE_URL)
                    .client(httpClient)
                    .build()
                    .create(ClientApi::class.java)

    override val catalogue: Observable<Catalogue>
        get() = restApi.loadCatalogue(ClientApi.API_KEY)

}