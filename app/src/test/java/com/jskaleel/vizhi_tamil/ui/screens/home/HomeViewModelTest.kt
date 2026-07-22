package com.jskaleel.vizhi_tamil.ui.screens.home

import com.jskaleel.vizhi_tamil.FakeOCRUseCase
import com.jskaleel.vizhi_tamil.MainDispatcherRule
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val scanA = ImageOCR("Invoice total", 90, "now", "/a.jpg", id = 1)
    private val scanB = ImageOCR("Grocery list", 70, "now", "/b.jpg", id = 2)

    @Test
    fun `empty scans produce Empty state`() = runTest {
        val useCase = FakeOCRUseCase(initialScans = emptyList())
        val viewModel = HomeViewModel(useCase)

        collectingUiState(viewModel) {
            advanceUntilIdle()
            assertEquals(HomeUiState.Empty, viewModel.uiState.value)
        }
    }

    @Test
    fun `search filters scans by recognised text`() = runTest {
        val useCase = FakeOCRUseCase(initialScans = listOf(scanA, scanB))
        val viewModel = HomeViewModel(useCase)

        collectingUiState(viewModel) {
            advanceUntilIdle()
            viewModel.onSearchChange("grocery")
            advanceUntilIdle()

            val state = viewModel.uiState.value as HomeUiState.Content
            assertEquals(listOf(scanB), state.scans)
            assertEquals("grocery", state.query)
        }
    }

    @Test
    fun `selection toggles and delete removes selected scans`() = runTest {
        val useCase = FakeOCRUseCase(initialScans = listOf(scanA, scanB))
        val viewModel = HomeViewModel(useCase)

        collectingUiState(viewModel) {
            advanceUntilIdle()
            viewModel.onToggleSelection(scanA.id)
            advanceUntilIdle()

            assertTrue((viewModel.uiState.value as HomeUiState.Content).inSelectionMode)

            viewModel.onDeleteSelected()
            advanceUntilIdle()

            assertEquals(listOf(listOf(scanA)), useCase.deleted)
            assertFalse((viewModel.uiState.value as HomeUiState.Content).inSelectionMode)
        }
    }

    @Test
    fun `onScanned emits OpenScan with the new id`() = runTest {
        val useCase = FakeOCRUseCase(initialScans = listOf(scanA))
        val viewModel = HomeViewModel(useCase)

        val received = mutableListOf<HomeEvent>()
        val job = backgroundScope.launch { viewModel.events.collect { received += it } }

        viewModel.onScanned(listOf("/tmp/page.jpg"))
        advanceUntilIdle()
        job.cancel()

        assertEquals(HomeEvent.OpenScan(42), received.first())
        assertEquals(listOf("/tmp/page.jpg"), useCase.lastRecognizedPaths)
    }

    /** Keeps uiState (a WhileSubscribed StateFlow) active for the duration of [block]. */
    private fun kotlinx.coroutines.test.TestScope.collectingUiState(
        viewModel: HomeViewModel,
        block: () -> Unit,
    ) {
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        block()
        job.cancel()
    }
}
