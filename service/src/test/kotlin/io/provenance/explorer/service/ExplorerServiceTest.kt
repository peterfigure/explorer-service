package io.provenance.explorer.service

import com.fasterxml.jackson.databind.JsonNode
import io.provenance.explorer.Application
import io.provenance.explorer.domain.RecentTx
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional


@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
open class ExplorerServiceTest {


    @Autowired
    lateinit var explorerService: ExplorerService


//    @Mock @Autowired lateinit var restTemplate: RestTemplate

    @Test
    @Ignore("TODO turn into integration tests")
    fun `should return recent blocks of different sizes and sort order`() {
        var result = explorerService.getRecentBlocks(25, 1, "")
        Assert.assertEquals(25, result.results.size)
        result = explorerService.getRecentBlocks(102, 0, "asc")
        Assert.assertEquals(102, result.results.size)
    }

    @Test
    @Ignore("TODO turn into integration tests")
    fun `should return a block at specific height`() {
        var result = explorerService.getBlockAtHeight(2840284)
    }

    @Test
    @Ignore("TODO turn into integration tests")
    fun `should return most recent transactions`() {
        val result = mutableListOf<RecentTx>()
        result.addAll(explorerService.getRecentTransactions(30, 0, "").results)
        result.addAll(explorerService.getRecentTransactions(30, 1, "").results)
        Assert.assertEquals(60, result.count())
    }

    @Test
    @Ignore("TODO turn into integration tests")
    fun `should return transaction by hash`() {
        val transferHash = "270384FE0D39104C3376BE96D6D71772EADA5CE21460CE2C889CF8873A5B4596"
        val nameBoundHash = "AF558EF8E903D76F60EE41D415276FED32A542FBF278EE9218C4619E3DB6F641"
        val transferTransaction = explorerService.getTransactionByHash(transferHash)
        val nameBoundTransaction = explorerService.getTransactionByHash(nameBoundHash)
        println("")
    }

    @Test
    @Ignore("TODO turn into integration tests")
    fun `get status`() {
        val result = explorerService.getStatus()
        println(result)
    }

    @Test
    @Ignore("TODO turn into integration tests")
    fun `get current height 2827095`() {
        val currentHeight = explorerService.getLatestBlockHeight()
        println(currentHeight)
    }

    @Test
    @Ignore("TODO turn into integration tests")
    fun `test latest height job`() {
        explorerService.updateLatestBlockHeight()
        explorerService.updateLatestBlockHeight()
    }

    @Test
    @Ignore("TODO turn into integration tests")
    fun `test get rest`() {
        val result = explorerService.getRestResult("https://test.provenance.io/explorer/sentinel/status")
        Assert.assertTrue(result is JsonNode)
    }

    @Test
    @Ignore("TODO turn into integration tests")
    fun `get current validators at height and page`() {
        val result = explorerService.getRecentValidators(11, 0, "asc")
        Assert.assertEquals(11, result.results.size)
        result.results.forEach { x -> Assert.assertEquals(4, x.validators.size) }
    }

    @Test
    @Ignore("")
    fun `test daily transaction count`() {
        explorerService.updateTodaysTransactionCount()
        val result = explorerService.getTransactionHistory("2020-11-13", "2020-11-13")
        explorerService.updateTodaysTransactionCount()
    }
}