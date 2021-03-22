package io.provenance.explorer.config

import com.google.protobuf.util.JsonFormat
import cosmos.auth.v1beta1.Auth
import cosmos.bank.v1beta1.Tx
import cosmos.crypto.ed25519.Keys
import cosmos.distribution.v1beta1.Distribution
import cosmos.gov.v1beta1.Gov
import cosmos.params.v1beta1.Params
import cosmos.tx.v1beta1.TxOuterClass
import cosmos.upgrade.v1beta1.Upgrade
import cosmos.vesting.v1beta1.Vesting
import io.provenance.attribute.v1.MsgAddAttributeRequest
import io.provenance.attribute.v1.MsgDeleteAttributeRequest
import io.provenance.marker.v1.AddMarkerProposal
import io.provenance.marker.v1.ChangeStatusProposal
import io.provenance.marker.v1.MarkerAccount
import io.provenance.marker.v1.MsgActivateRequest
import io.provenance.marker.v1.MsgAddAccessRequest
import io.provenance.marker.v1.MsgAddMarkerRequest
import io.provenance.marker.v1.MsgBurnRequest
import io.provenance.marker.v1.MsgCancelRequest
import io.provenance.marker.v1.MsgDeleteAccessRequest
import io.provenance.marker.v1.MsgDeleteRequest
import io.provenance.marker.v1.MsgFinalizeRequest
import io.provenance.marker.v1.MsgMintRequest
import io.provenance.marker.v1.MsgSetDenomMetadataRequest
import io.provenance.marker.v1.MsgTransferRequest
import io.provenance.marker.v1.MsgWithdrawRequest
import io.provenance.marker.v1.RemoveAdministratorProposal
import io.provenance.marker.v1.SetAdministratorProposal
import io.provenance.marker.v1.SupplyDecreaseProposal
import io.provenance.marker.v1.SupplyIncreaseProposal
import io.provenance.metadata.v1.MsgAddContractSpecificationRequest
import io.provenance.metadata.v1.MsgAddRecordRequest
import io.provenance.metadata.v1.MsgAddRecordSpecificationRequest
import io.provenance.metadata.v1.MsgAddScopeRequest
import io.provenance.metadata.v1.MsgAddScopeSpecificationRequest
import io.provenance.metadata.v1.MsgAddSessionRequest
import io.provenance.metadata.v1.MsgChangeOwnershipRequest
import io.provenance.metadata.v1.MsgDeleteContractSpecificationRequest
import io.provenance.metadata.v1.MsgDeleteRecordRequest
import io.provenance.metadata.v1.MsgDeleteRecordSpecificationRequest
import io.provenance.metadata.v1.MsgDeleteScopeRequest
import io.provenance.metadata.v1.MsgDeleteScopeSpecificationRequest
import io.provenance.metadata.v1.MsgMemorializeContractRequest
import io.provenance.name.v1.CreateRootNameProposal
import io.provenance.name.v1.MsgBindNameRequest
import io.provenance.name.v1.MsgDeleteNameRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class RestConfig {

    @Bean
    fun protoPrinter(): JsonFormat.Printer? {
        val typeRegistry = JsonFormat.TypeRegistry.newBuilder()
            .add(accountDescriptors())
            .add(pubKeyDescriptors())
            .add(msgDescriptors())
            .add(contentDescriptors())
            .build()
        return JsonFormat.printer().usingTypeRegistry(typeRegistry)
    }

    @Bean
    fun protoParser(): JsonFormat.Parser? {
        val typeRegistry = JsonFormat.TypeRegistry.newBuilder()
            .add(accountDescriptors())
            .add(pubKeyDescriptors())
            .add(msgDescriptors())
            .add(contentDescriptors())
            .build()
        return JsonFormat.parser().usingTypeRegistry(typeRegistry)
    }

    @Bean
    @Primary
    fun protobufJsonFormatHttpMessageConverter(): ProtobufHttpMessageConverter? {
        return ProtobufJsonFormatHttpMessageConverter(protoParser(), protoPrinter())
    }

    @Bean
    fun restTemplate(hmc: ProtobufHttpMessageConverter?): RestTemplate? {
        return RestTemplate(listOf(hmc))
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            @Override
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**")
                    .allowedMethods("*")
                    .allowedOriginPatterns("*")
                    .allowCredentials(true)
                    .maxAge(3600)
            }
        }
    }
}

fun accountDescriptors() =
    listOf(
        MarkerAccount.getDescriptor(),
        Auth.BaseAccount.getDescriptor(),
        Auth.ModuleAccount.getDescriptor(),
        Vesting.BaseVestingAccount.getDescriptor(),
        Vesting.ContinuousVestingAccount.getDescriptor(),
        Vesting.DelayedVestingAccount.getDescriptor(),
        Vesting.PeriodicVestingAccount.getDescriptor()
    )

fun pubKeyDescriptors() =
    listOf(
        Keys.PubKey.getDescriptor(),
        cosmos.crypto.secp256k1.Keys.PubKey.getDescriptor(),
        cosmos.crypto.multisig.Keys.LegacyAminoPubKey.getDescriptor()
    )

fun msgDescriptors() =
    listOf(
        TxOuterClass.Tx.getDescriptor(),
        Tx.MsgSend.getDescriptor(),
        Tx.MsgMultiSend.getDescriptor(),
        cosmos.gov.v1beta1.Tx.MsgSubmitProposal.getDescriptor(),
        cosmos.gov.v1beta1.Tx.MsgVote.getDescriptor(),
        cosmos.gov.v1beta1.Tx.MsgDeposit.getDescriptor(),
        cosmos.distribution.v1beta1.Tx.MsgSetWithdrawAddress.getDescriptor(),
        cosmos.distribution.v1beta1.Tx.MsgWithdrawDelegatorReward.getDescriptor(),
        cosmos.distribution.v1beta1.Tx.MsgWithdrawValidatorCommission.getDescriptor(),
        cosmos.distribution.v1beta1.Tx.MsgFundCommunityPool.getDescriptor(),
        cosmos.evidence.v1beta1.Tx.MsgSubmitEvidence.getDescriptor(),
        cosmos.slashing.v1beta1.Tx.MsgUnjail.getDescriptor(),
        cosmos.staking.v1beta1.Tx.MsgCreateValidator.getDescriptor(),
        cosmos.staking.v1beta1.Tx.MsgEditValidator.getDescriptor(),
        cosmos.staking.v1beta1.Tx.MsgDelegate.getDescriptor(),
        cosmos.staking.v1beta1.Tx.MsgBeginRedelegate.getDescriptor(),
        cosmos.staking.v1beta1.Tx.MsgUndelegate.getDescriptor(),
        cosmos.vesting.v1beta1.Tx.MsgCreateVestingAccount.getDescriptor(),
        MsgWithdrawRequest.getDescriptor(),
        MsgAddMarkerRequest.getDescriptor(),
        MsgAddAccessRequest.getDescriptor(),
        MsgDeleteAccessRequest.getDescriptor(),
        MsgFinalizeRequest.getDescriptor(),
        MsgActivateRequest.getDescriptor(),
        MsgCancelRequest.getDescriptor(),
        MsgDeleteRequest.getDescriptor(),
        MsgMintRequest.getDescriptor(),
        MsgBurnRequest.getDescriptor(),
        MsgTransferRequest.getDescriptor(),
        MsgSetDenomMetadataRequest.getDescriptor(),
        MsgBindNameRequest.getDescriptor(),
        MsgDeleteNameRequest.getDescriptor(),
        MsgAddAttributeRequest.getDescriptor(),
        MsgDeleteAttributeRequest.getDescriptor(),
        MsgMemorializeContractRequest.getDescriptor(),
        MsgChangeOwnershipRequest.getDescriptor(),
        MsgAddScopeRequest.getDescriptor(),
        MsgDeleteScopeRequest.getDescriptor(),
        MsgAddSessionRequest.getDescriptor(),
        MsgAddRecordRequest.getDescriptor(),
        MsgDeleteRecordRequest.getDescriptor(),
        MsgAddScopeSpecificationRequest.getDescriptor(),
        MsgDeleteScopeSpecificationRequest.getDescriptor(),
        MsgAddContractSpecificationRequest.getDescriptor(),
        MsgDeleteContractSpecificationRequest.getDescriptor(),
        MsgAddRecordSpecificationRequest.getDescriptor(),
        MsgDeleteRecordSpecificationRequest.getDescriptor()
    )

fun contentDescriptors() =
    listOf(
        Gov.TextProposal.getDescriptor(),
        Params.ParameterChangeProposal.getDescriptor(),
        Upgrade.SoftwareUpgradeProposal.getDescriptor(),
        Upgrade.CancelSoftwareUpgradeProposal.getDescriptor(),
        Distribution.CommunityPoolSpendProposal.getDescriptor(),
        AddMarkerProposal.getDescriptor(),
        SupplyIncreaseProposal.getDescriptor(),
        SupplyDecreaseProposal.getDescriptor(),
        SetAdministratorProposal.getDescriptor(),
        RemoveAdministratorProposal.getDescriptor(),
        ChangeStatusProposal.getDescriptor(),
        CreateRootNameProposal.getDescriptor()
    )