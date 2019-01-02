package com.igordanilchik.daggertest.flows.catalogue.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import butterknife.BindView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.igordanilchik.daggertest.R
import com.igordanilchik.daggertest.common.mvp.view.BaseFragment
import com.igordanilchik.daggertest.data.Categories
import com.igordanilchik.daggertest.flows.catalogue.builder.CatalogueModule
import com.igordanilchik.daggertest.flows.catalogue.presenter.CataloguePresenter
import com.igordanilchik.daggertest.ui.ViewContract
import com.igordanilchik.daggertest.ui.activity.MainActivity
import com.igordanilchik.daggertest.ui.adapter.CategoriesAdapter

/**
 * @author Igor Danilchik
 */
class CatalogueFragment: BaseFragment(), CatalogueView, CategoriesAdapter.CategoriesCallback {

    @BindView(R.id.catalogue_recycler_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.swipe_container)
    lateinit var swipeContainer: SwipeRefreshLayout
    @BindView(R.id.empty_state_container)
    lateinit var emptyStateContainer: LinearLayout


    @InjectPresenter
    lateinit var presenter: CataloguePresenter

    override val layoutResID = R.layout.fragment_catalogue

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeContainer.setOnRefreshListener(presenter::onRefresh)
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    override fun onDestroyView() {
        recyclerView.adapter = null

        super.onDestroyView()
    }

    override fun onCategoryClicked(category: Categories.Category) =
            presenter.onCategoryClicked(category)


    override fun showCategories(categories: Categories) {
        (recyclerView.adapter as? CategoriesAdapter)?.apply {
            appendOrUpdate(categories.categories)
        } ?: run {
            recyclerView.adapter = CategoriesAdapter(categories, this)
        }
    }

    override fun showError(throwable: Throwable) {
        Snackbar.make(recyclerView, "Error: " + throwable.message, Snackbar.LENGTH_LONG)
                .show()
    }

    override fun showProgress() {
        swipeContainer.post { swipeContainer.isRefreshing = true }
    }

    override fun hideProgress() {
        swipeContainer.post { swipeContainer.isRefreshing = false }
    }

    override fun showEmptyState() {
        emptyStateContainer.visibility = View.VISIBLE
    }

    override fun hideEmptyState() {
        emptyStateContainer.visibility = View.GONE
    }

    override fun goToCategory(id: Int) {
        val bundle = Bundle()
        bundle.putInt(MainActivity.ARG_CATEGORY_ID, id)

        (activity as ViewContract).goToCategory(bundle)
    }

    @ProvidePresenter
    fun providePresenter(): CataloguePresenter {
        return appComponent().plusCatalogueComponent(CatalogueModule()).presenter()
    }

}