package com.igordanilchik.daggertest.flows.offers.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.BindView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.igordanilchik.daggertest.R
import com.igordanilchik.daggertest.common.mvp.view.BaseFragment
import com.igordanilchik.daggertest.data.Offers
import com.igordanilchik.daggertest.flows.offers.builder.OffersModule
import com.igordanilchik.daggertest.flows.offers.model.OffersSupplier
import com.igordanilchik.daggertest.flows.offers.presenter.OffersPresenter
import com.igordanilchik.daggertest.ui.ViewContract
import com.igordanilchik.daggertest.ui.activity.MainActivity
import com.igordanilchik.daggertest.ui.adapter.OffersAdapter

/**
 * @author Igor Danilchik
 */
class OffersFragment: BaseFragment(), OffersView, OffersAdapter.OffersCallback {

    @BindView(R.id.offers_recycler_view)
    lateinit var recyclerView: RecyclerView

    @InjectPresenter
    lateinit var presenter: OffersPresenter

    override val layoutResID = R.layout.fragment_offers

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    override fun onDestroyView() {
        recyclerView.adapter = null

        super.onDestroyView()
    }

    override fun onOfferClicked(offer: Offers.Offer) = presenter.onOfferClicked(offer)

    override fun showOffers(offers: Offers) {
        (recyclerView.adapter as? OffersAdapter)?.apply {
            appendOrUpdate(offers.offers)
        } ?: run {
            recyclerView.adapter = OffersAdapter(offers, this)
        }
    }

    override fun showError(throwable: Throwable) {
        Snackbar.make(recyclerView, "Error: " + throwable.message, Snackbar.LENGTH_LONG)
                .show()
    }

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

    override fun goToOffer(id: Int) {
        val bundle = Bundle()
        bundle.putInt(MainActivity.ARG_OFFER_ID, id)

        (activity as ViewContract).goToOffer(bundle)
    }

    @ProvidePresenter
    fun providePresenter(): OffersPresenter {
        val bundle = arguments ?: Bundle()
        val supplier = OffersSupplier(id = bundle.getInt(MainActivity.ARG_CATEGORY_ID))

        return appComponent().plusOffersComponent(OffersModule(supplier)).presenter()
    }

}