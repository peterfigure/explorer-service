package io.provenance.explorer.web.v2

import io.provenance.explorer.service.NftService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Min

@Validated
@RestController
@RequestMapping(path = ["/api/v2/nft"], produces = [MediaType.APPLICATION_JSON_VALUE])
@Api(value = "NFT controller", produces = "application/json", consumes = "application/json", tags = ["NFTs"])
class NftController(private val nftService: NftService) {

    @ApiOperation("Returns NFT detail for address")
    @GetMapping("/scope/{addr}")
    fun getNftDetail(@PathVariable addr: String) = ResponseEntity.ok(nftService.getScopeDetail(addr))

    @ApiOperation("Returns NFTs for owning address")
    @GetMapping("/scope/owner/{address}")
    fun getNftsByOwningAddress(
        @PathVariable address: String,
        @RequestParam(required = false, defaultValue = "1") @Min(1) page: Int,
        @RequestParam(required = false, defaultValue = "10") @Min(1) count: Int
    ) = ResponseEntity.ok(nftService.getScopesForOwningAddress(address, page, count))

    @ApiOperation("Returns MetadataAddress obj for bech32 addr")
    @GetMapping("/address/{addr}")
    fun getMetadataAddress(@PathVariable addr: String) = ResponseEntity.ok(nftService.translateAddress(addr))

    @ApiOperation("Returns records for the NFT")
    @GetMapping("/scope/{addr}/records")
    fun getNftRecords(@PathVariable addr: String) = ResponseEntity.ok(nftService.getRecordsForScope(addr))

    @ApiOperation("Returns the scope spec as a json object")
    @GetMapping("/scopeSpec/{scopeSpec}/json")
    fun getScopeSpecJson(@PathVariable scopeSpec: String) = ResponseEntity.ok(nftService.getScopeSpecJson(scopeSpec))

    @ApiOperation("Returns the contract spec as a json object")
    @GetMapping("/contractSpec/{contractSpec}/json")
    fun getContractSpecJson(@PathVariable contractSpec: String) = ResponseEntity.ok(nftService.getContractSpecJson(contractSpec))

    @ApiOperation("Returns the record spec as a json object")
    @GetMapping("/recordSpec/{recordSpec}/json")
    fun getRecordSpecJson(@PathVariable recordSpec: String) = ResponseEntity.ok(nftService.getRecordSpecJson(recordSpec))
}
