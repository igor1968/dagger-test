package com.igordanilchik.daggertest.flows.offer.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.igordanilchik.daggertest.R
import com.igordanilchik.daggertest.common.mvp.view.BaseFragment
import com.igordanilchik.daggertest.data.Offers
import com.igordanilchik.daggertest.flows.offer.builder.OfferModule
import com.igordanilchik.daggertest.flows.offer.model.OfferSupplier
import com.igordanilchik.daggertest.flows.offer.presenter.OfferPresenter
import com.igordanilchik.daggertest.ui.activity.MainActivity

/**
 * @author Igor Danilchik
 */
class OfferFragment : BaseFragment(), OfferView {

    @BindView(R.id.card_image)
    lateinit var image: ImageView
    @BindView(R.id.card_title)
    lateinit var title: TextView
    @BindView(R.id.card_price)
    lateinit var price: TextView
    @BindView(R.id.card_weight)
    lateinit var weight: TextView
    @BindView(R.id.card_description)
    lateinit var description: TextView
    @BindView(R.id.linear_layout)
    lateinit var linearLayout: LinearLayout

    @InjectPresenter
    lateinit var presenter: OfferPresenter

    override val layoutResID = R.layout.fragment_offer

    override fun showOffer(offer: Offers.Offer) {
        title.text = offer.name
        price.text = getString(R.string.offer_price, offer.price)

        getParamByKey(offer, getString(R.string.param_name_weight)).let {
            weight.text = getString(R.string.offer_weight, it)
        }

        offer.picture?.let { url ->
            url.takeIf { it.isNotEmpty() }?.let {
                Glide.with(this)
                        .load(it)
                        .fitCenter()
                        .placeholder(context?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.ic_image_black_24dp) })
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(image)
            }
        } ?: run { image.visibility = View.GONE }

        description.text = offer.description
    }

    private fun getParamByKey(list: Offers.Offer?, key: String): String? = list?.param?.find { it.name == key }?.value

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

    override fun showError(throwable: Throwable) {
        Snackbar.make(linearLayout, "Error: " + throwable.message, Snackbar.LENGTH_LONG)
                .show()
    }

    @ProvidePresenter
    fun providePresenter(): OfferPresenter {
        val bundle = arguments ?: Bundle()
        val supplier = OfferSupplier(id = bundle.getInt(MainActivity.ARG_OFFER_ID))

        return appComponent().plusOfferComponent(OfferModule(supplier)).presenter()
    }


}