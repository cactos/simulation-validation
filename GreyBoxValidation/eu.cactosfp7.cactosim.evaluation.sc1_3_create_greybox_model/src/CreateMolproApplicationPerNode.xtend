import eu.cactosfp7.cactosim.experimentscenario.ExperimentscenarioPackage
import eu.cactosfp7.cactosim.experimentscenario.util.ExperimentscenarioResourceFactoryImpl
import eu.cactosfp7.infrastructuremodels.logicaldc.application.ApplicationFactory
import eu.cactosfp7.infrastructuremodels.logicaldc.application.GreyBoxApplicationTemplate
import eu.cactosfp7.infrastructuremodels.logicaldc.core.CoreFactory
import eu.cactosfp7.infrastructuremodels.logicaldc.core.Hypervisor
import eu.cactosfp7.infrastructuremodels.logicaldc.core.LogicalDCModel
import eu.cactosfp7.infrastructuremodels.logicaldc.core.VMImage
import eu.cactosfp7.infrastructuremodels.logicaldc.core.impl.CorePackageImpl
import eu.cactosfp7.infrastructuremodels.logicaldc.hypervisor.impl.HypervisorPackageImpl
import eu.cactosfp7.infrastructuremodels.logicaldc.hypervisor.util.HypervisorResourceFactoryImpl
import eu.cactosfp7.infrastructuremodels.physicaldc.architecturetype.ArchitectureTypeRepository
import eu.cactosfp7.infrastructuremodels.physicaldc.architecturetype.impl.ArchitecturetypePackageImpl
import eu.cactosfp7.infrastructuremodels.physicaldc.architecturetype.util.ArchitecturetypeResourceFactoryImpl
import eu.cactosfp7.infrastructuremodels.physicaldc.core.ProcessingUnitSpecification
import eu.cactosfp7.infrastructuremodels.physicaldc.core.StorageSpecification
import eu.cactosfp7.infrastructuremodels.physicaldc.core.util.CoreResourceFactoryImpl
import eu.cactosfp7.infrastructuremodels.physicaldc.power.binding.impl.BindingPackageImpl
import eu.cactosfp7.infrastructuremodels.physicaldc.power.binding.util.BindingResourceFactoryImpl
import eu.cactosfp7.infrastructuremodels.physicaldc.power.specification.impl.SpecificationPackageImpl
import eu.cactosfp7.infrastructuremodels.physicaldc.power.specification.util.SpecificationResourceFactoryImpl
import eu.cactosfp7.infrastructuremodels.physicaldc.util.UtilFactory
import java.util.Collections
import javax.measure.quantity.DataRate
import javax.measure.quantity.Dimensionless
import javax.measure.unit.NonSI
import javax.measure.unit.SI
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.jscience.physics.amount.Amount

class CreateMolproApplicationPerNode {
	static val MODELNAME = "uulm-testbed"
	
	val LogicalDCModel logicalDC
	val ArchitectureTypeRepository architectureTypeRepository
	 
	def static void main(String[] args) {
		val resourceSet = new ResourceSetImpl()
		
		resourceSet.packageRegistry => [
			put(ArchitecturetypePackageImpl.eNS_URI, ArchitecturetypePackageImpl.eINSTANCE)
			put(HypervisorPackageImpl.eNS_URI, HypervisorPackageImpl.eINSTANCE)
			put(BindingPackageImpl.eNS_URI, BindingPackageImpl.eINSTANCE)
			put(SpecificationPackageImpl.eNS_URI, SpecificationPackageImpl.eINSTANCE)
			put(CorePackageImpl.eNS_URI, CorePackageImpl.eINSTANCE)
			put(eu.cactosfp7.infrastructuremodels.physicaldc.core.impl.CorePackageImpl.eNS_URI, eu.cactosfp7.infrastructuremodels.physicaldc.core.impl.CorePackageImpl.eINSTANCE)
			put(ExperimentscenarioPackage.eNS_URI, ExperimentscenarioPackage.eINSTANCE)
		]
		
		Resource.Factory.Registry.INSTANCE.extensionToFactoryMap => [
			put("architecturetype", new ArchitecturetypeResourceFactoryImpl)
	    	put("hypervisor", new HypervisorResourceFactoryImpl)
	    	put("pdcbinding", new BindingResourceFactoryImpl)
	    	put("pdcspec", new SpecificationResourceFactoryImpl)
	    	put("physical", new CoreResourceFactoryImpl)
	    	put("logical", new eu.cactosfp7.infrastructuremodels.logicaldc.core.util.CoreResourceFactoryImpl)
	    	put("experimentscenario", new ExperimentscenarioResourceFactoryImpl)
	    ]
	    
	    val archTypeResource = resourceSet.getResource(URI.createURI(MODELNAME + ".architecturetype"), true)
	    val logicalResource = resourceSet.getResource(URI.createURI(MODELNAME + ".logical"), true) 
	    
	    
	    new CreateMolproApplicationPerNode(
	    	logicalResource.contents.filter(LogicalDCModel).head,
	    	archTypeResource.contents.filter(ArchitectureTypeRepository).head
	    ).createAMolproLCCSDInstancePerHypervisor
	    
	    logicalResource.save(Collections.emptyMap)
	}
	
	new(LogicalDCModel model, ArchitectureTypeRepository archRepo) {
		this.logicalDC = model
		this.architectureTypeRepository = archRepo
	}
	
	def createAMolproLCCSDInstancePerHypervisor() {
		createFlavour
		createVMImage
		createApplicationTemplate
		logicalDC.hypervisors.forEach[createApplicationInstance]
	}
	
	def createFlavour() {
		if (this.logicalDC.flavours.filter[it.name == "Molpro Generic SingleCore Flavour"].size == 0) {
			this.logicalDC.flavours += CoreFactory.INSTANCE.createFlavour => [
				name = "Molpro Generic SingleCore Flavour"
				architectureType = architectureTypeRepository.architectureTypes.head
				numberVirtualCores = 1
				sizeRam = Amount.valueOf(4.0, SI.GIGA(NonSI.BYTE))
				sizeStorage = Amount.valueOf(20.0, SI.GIGA(NonSI.BYTE))
				flavourRefVMI = "XXX"
			]
		}
	}
	
	def createVMImage() {
		if (this.logicalDC.volumesAndImages.filter(VMImage).filter[name == "Molpro LCCSD VM Image"].size == 0) {
			this.logicalDC.volumesAndImages += CoreFactory.INSTANCE.createVMImage => [
				name = "Molpro LCCSD VM Image"
				capacity = Amount.valueOf(20.0, SI.GIGA(NonSI.BYTE))
				usedCapacity = Amount.valueOf(5.0, SI.GIGA(NonSI.BYTE))
				storageLocation = this.logicalDC.hypervisors.head.node.storageSpecifications.head
			] 
		}
	}
	
	def createApplicationTemplate() {
		val physiFac = eu.cactosfp7.infrastructuremodels.physicaldc.core.CoreFactory.INSTANCE
		if (this.logicalDC.applicationTemplates.filter(GreyBoxApplicationTemplate).filter[name == "Molpro LCCSD Application Template"].size == 0) {
			this.logicalDC.applicationTemplates += ApplicationFactory.INSTANCE.createGreyBoxApplicationTemplate => [
				name = "Molpro LCCSD Application Template"
				greyBoxVMImageBehaviour = ApplicationFactory.INSTANCE.createGreyBoxVMImageBehaviour => [
					vmImage = this.logicalDC.volumesAndImages.filter[name == "Molpro LCCSD VM Image"].head
					val refCPU = physiFac.createProcessingUnitSpecification => [
						name = "Reference Processing Unit Specification"
						frequency = Amount.valueOf(3.2, SI.GIGA(SI.HERTZ))
						numberOfCores = 1
						architectureType = architectureTypeRepository.architectureTypes.head
					]
					referenceResourceSpecifications += refCPU
					val refStorage = physiFac.createStorageSpecification => [
						name = "Reference Storage Specification"
						size = Amount.valueOf(220, SI.GIGA(NonSI.BYTE))
						readDelay = Amount.valueOf(0, SI.SECOND)
						writeDelay = Amount.valueOf(0, SI.SECOND)
						readBandwidth = UtilFactory.INSTANCE.createBandwidth => [
							value = Amount.valueOf(960.0, SI.MEGA(DataRate.UNIT))
						]
						writeBandwidth = UtilFactory.INSTANCE.createBandwidth => [
							value = Amount.valueOf(960.0, SI.MEGA(DataRate.UNIT))
						]
					]
					referenceResourceSpecifications += refStorage
					workloadPhases += ApplicationFactory.INSTANCE.createWorkloadPhase => [
						name = "Molpro LCCSD Phase 1"
						resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [
							amount = Amount.valueOf(35374, SI.GIGA(Dimensionless.UNIT))
							onResource = refCPU
						]
						resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [
							amount = Amount.valueOf(189, SI.GIGA(NonSI.BYTE))
							onResource = refStorage
						]
					]
					workloadPhases += ApplicationFactory.INSTANCE.createWorkloadPhase => [
						name = "Molpro LCCSD Phase 2"
						resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [
							amount = Amount.valueOf(115, SI.GIGA(Dimensionless.UNIT))
							onResource = refCPU
						]
						resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [
							amount = Amount.valueOf(234, SI.GIGA(NonSI.BYTE))
							onResource = refStorage
						]
					]
					workloadPhases += ApplicationFactory.INSTANCE.createWorkloadPhase => [
						name = "Molpro LCCSD Phase 3"
						resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [
							amount = Amount.valueOf(28259, SI.GIGA(Dimensionless.UNIT))
							onResource = refCPU
						]
						resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [
							amount = Amount.valueOf(172, SI.GIGA(NonSI.BYTE))
							onResource = refStorage
						]
					]
					workloadPhases += ApplicationFactory.INSTANCE.createWorkloadPhase => [
						name = "Molpro LCCSD Phase 4"
						resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [
							amount = Amount.valueOf(2212, SI.GIGA(Dimensionless.UNIT))
							onResource = refCPU
						]
						resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [
							amount = Amount.valueOf(422, SI.GIGA(NonSI.BYTE))
							onResource = refStorage
						]
					]
				]
			]
		}
	}
	
	def createApplicationInstance(Hypervisor forHypervisor) {
		val appTemplate = logicalDC.applicationTemplates.filter(GreyBoxApplicationTemplate).filter[name == "Molpro LCCSD Application Template"].head
		if (forHypervisor.virtualMachines.filter[runtimeApplicationModel.vmImageBehaviour.id == appTemplate.greyBoxVMImageBehaviour.id].size == 0) {
			val newVM = CoreFactory.INSTANCE.createVirtualMachine => [vm |
				vm.name = "Molpro LCCSD Virtual Machine"
				vm.virtualMemoryUnits += CoreFactory.INSTANCE.createVirtualMemory => [
					provisioned = Amount.valueOf(8, SI.GIGA(NonSI.BYTE))
				] 
				val vdisk = CoreFactory.INSTANCE.createVirtualDisk => [
					name = "Disk Overlay for VM on Hypervisor of " + forHypervisor.node.name
					storageLocation = forHypervisor.rootVolume.storageLocation
					capacity = Amount.valueOf(20, SI.GIGA(NonSI.BYTE))
					usedCapacity = Amount.valueOf(20, SI.GIGA(NonSI.BYTE))
					appTemplate.greyBoxVMImageBehaviour.vmImage.deltaOverlay += it
				]
				vm.VMImageInstance = CoreFactory.INSTANCE.createVMImageInstance => [
					rootDisk = vdisk
				]
				val vcpu = CoreFactory.INSTANCE.createVirtualProcessingUnit => [
					name = "Generic SingleCore VCPU"
					architectureType = architectureTypeRepository.architectureTypes.head
					virtualCores = 1
					vm.virtualProcessingUnits += it
				]
				vm.runtimeApplicationModel = ApplicationFactory.INSTANCE.createGreyBoxVMBehaviour => [vmb |
					vmb.name = "GreyBoxVMBehaviour of " + vm.name
					vmb.vmImageBehaviour = appTemplate.greyBoxVMImageBehaviour
					appTemplate.greyBoxVMImageBehaviour.workloadPhases.forEach[templatePhase |
						vmb.workloadPhases += ApplicationFactory.INSTANCE.createWorkloadPhase => [newPhase |
							newPhase.name = templatePhase.name
							templatePhase.resourceDemands.forEach[templateDemand |
								newPhase.resourceDemands += ApplicationFactory.INSTANCE.createResourceDemand => [newDemand |
									newDemand.onResource = templateDemand.onResource
									newDemand.amount = templateDemand.amount
								]
							]
						]
					]
				]
				forHypervisor.virtualMachines += vm
			] 
			logicalDC.applicationInstances += ApplicationFactory.INSTANCE.createGreyBoxApplicationInstance => [
				virtualMachine = newVM
				applicationTemplate = appTemplate
			]
		}
	}
}