package com.igordanilchik.daggertest.flows.location.builder

import android.app.Application
import android.content.Context
import com.igordanilchik.daggertest.flows.location.model.ILocationModel
import com.igordanilchik.daggertest.flows.location.model.LocationModel
import com.igordanilchik.daggertest.flows.location.presenter.LocationPresenter
import com.igordanilchik.daggertest.repo.SchedulersSet
import com.vanniktech.rxpermission.RealRxPermission
import dagger.Module
import dagger.Provides
import pl.charmas.android.reactivelocation.ReactiveLocationProvider

/**
 * @author Igor Danilchik
 */
@Module
class LocationModule {

    @Provides
    fun presenter(
            schedulersSet: SchedulersSet,
            model: ILocationModel
    ): LocationPresenter = LocationPresenter(
            schedulersSet,
            model
    )

    @Provides
    fun model(
            locationProvider: ReactiveLocationProvider,
            permissionProvider: RealRxPermission
    ): ILocationModel =
            LocationModel(
                    locationProvider,
                    permissionProvider
            )

    @Provides
    fun locationProvider(context: Context): ReactiveLocationProvider =
            ReactiveLocationProvider(context)

    @Provides
    fun permissionProvider(application: Application): RealRxPermission =
            RealRxPermission.getInstance(application)

}