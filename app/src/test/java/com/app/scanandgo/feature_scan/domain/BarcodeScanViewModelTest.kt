package com.app.scanandgo.feature_scan.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.scanandgo.feature_scan.data.ProductRepository
import com.app.scanandgo.getOrAwaitValueTest
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BarcodeScanViewModelTest {

    private lateinit var barcodeScanViewModel: BarcodeScanViewModel

    private lateinit var fakeRepository: FakeRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(newSingleThreadContext("UI thread"))
    }

    @Test
    fun `Test Success case of API Call`() = runBlockingTest{

        fakeRepository = FakeRepository()
        val fakeResponse = fakeRepository.getProduct()

        whenever(productRepository.getProduct(1)).thenReturn(fakeResponse)

        barcodeScanViewModel = BarcodeScanViewModel(productRepository)

        barcodeScanViewModel.fetchProduct(1)

        //Test response of one live data gets response in observer
        val productResult = barcodeScanViewModel.productResult.getOrAwaitValueTest()
        assertThat(productResult.data).isNotNull()

    }

}