import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import eu.cactosfp7.cactosim.experimentscenario.ExperimentscenarioPackage;
import eu.cactosfp7.cactosim.experimentscenario.util.ExperimentscenarioResourceFactoryImpl;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.ApplicationFactory;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.ApplicationInstance;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.ApplicationTemplate;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.GreyBoxApplicationInstance;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.GreyBoxApplicationTemplate;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.GreyBoxVMBehaviour;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.GreyBoxVMImageBehaviour;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.ResourceDemand;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.VMBehaviour;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.VMImageBehaviour;
import eu.cactosfp7.infrastructuremodels.logicaldc.application.WorkloadPhase;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.CoreFactory;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.Flavour;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.Hypervisor;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.LogicalDCModel;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.VMImage;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.VMImageInstance;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.VirtualDisk;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.VirtualMachine;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.VirtualMemory;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.VirtualProcessingUnit;
import eu.cactosfp7.infrastructuremodels.logicaldc.core.impl.CorePackageImpl;
import eu.cactosfp7.infrastructuremodels.logicaldc.hypervisor.impl.HypervisorPackageImpl;
import eu.cactosfp7.infrastructuremodels.logicaldc.hypervisor.util.HypervisorResourceFactoryImpl;
import eu.cactosfp7.infrastructuremodels.physicaldc.architecturetype.ArchitectureType;
import eu.cactosfp7.infrastructuremodels.physicaldc.architecturetype.ArchitectureTypeRepository;
import eu.cactosfp7.infrastructuremodels.physicaldc.architecturetype.impl.ArchitecturetypePackageImpl;
import eu.cactosfp7.infrastructuremodels.physicaldc.architecturetype.util.ArchitecturetypeResourceFactoryImpl;
import eu.cactosfp7.infrastructuremodels.physicaldc.core.ComputeNode;
import eu.cactosfp7.infrastructuremodels.physicaldc.core.MonitorableResource;
import eu.cactosfp7.infrastructuremodels.physicaldc.core.ProcessingUnitSpecification;
import eu.cactosfp7.infrastructuremodels.physicaldc.core.StorageSpecification;
import eu.cactosfp7.infrastructuremodels.physicaldc.core.util.CoreResourceFactoryImpl;
import eu.cactosfp7.infrastructuremodels.physicaldc.power.binding.impl.BindingPackageImpl;
import eu.cactosfp7.infrastructuremodels.physicaldc.power.binding.util.BindingResourceFactoryImpl;
import eu.cactosfp7.infrastructuremodels.physicaldc.power.specification.impl.SpecificationPackageImpl;
import eu.cactosfp7.infrastructuremodels.physicaldc.power.specification.util.SpecificationResourceFactoryImpl;
import eu.cactosfp7.infrastructuremodels.physicaldc.util.Bandwidth;
import eu.cactosfp7.infrastructuremodels.physicaldc.util.UtilFactory;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import javax.measure.quantity.DataAmount;
import javax.measure.quantity.DataRate;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Frequency;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.jscience.physics.amount.Amount;

@SuppressWarnings("all")
public class CreateMolproApplicationPerNode {
  private final static String MODELNAME = "uulm-testbed";
  
  private final LogicalDCModel logicalDC;
  
  private final ArchitectureTypeRepository architectureTypeRepository;
  
  public static void main(final String[] args) {
    try {
      final ResourceSetImpl resourceSet = new ResourceSetImpl();
      EPackage.Registry _packageRegistry = resourceSet.getPackageRegistry();
      final Procedure1<EPackage.Registry> _function = (EPackage.Registry it) -> {
        it.put(ArchitecturetypePackageImpl.eNS_URI, ArchitecturetypePackageImpl.eINSTANCE);
        it.put(HypervisorPackageImpl.eNS_URI, HypervisorPackageImpl.eINSTANCE);
        it.put(BindingPackageImpl.eNS_URI, BindingPackageImpl.eINSTANCE);
        it.put(SpecificationPackageImpl.eNS_URI, SpecificationPackageImpl.eINSTANCE);
        it.put(CorePackageImpl.eNS_URI, CorePackageImpl.eINSTANCE);
        it.put(eu.cactosfp7.infrastructuremodels.physicaldc.core.impl.CorePackageImpl.eNS_URI, eu.cactosfp7.infrastructuremodels.physicaldc.core.impl.CorePackageImpl.eINSTANCE);
        it.put(ExperimentscenarioPackage.eNS_URI, ExperimentscenarioPackage.eINSTANCE);
      };
      ObjectExtensions.<EPackage.Registry>operator_doubleArrow(_packageRegistry, _function);
      Map<String, Object> _extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      final Procedure1<Map<String, Object>> _function_1 = (Map<String, Object> it) -> {
        ArchitecturetypeResourceFactoryImpl _architecturetypeResourceFactoryImpl = new ArchitecturetypeResourceFactoryImpl();
        it.put("architecturetype", _architecturetypeResourceFactoryImpl);
        HypervisorResourceFactoryImpl _hypervisorResourceFactoryImpl = new HypervisorResourceFactoryImpl();
        it.put("hypervisor", _hypervisorResourceFactoryImpl);
        BindingResourceFactoryImpl _bindingResourceFactoryImpl = new BindingResourceFactoryImpl();
        it.put("pdcbinding", _bindingResourceFactoryImpl);
        SpecificationResourceFactoryImpl _specificationResourceFactoryImpl = new SpecificationResourceFactoryImpl();
        it.put("pdcspec", _specificationResourceFactoryImpl);
        CoreResourceFactoryImpl _coreResourceFactoryImpl = new CoreResourceFactoryImpl();
        it.put("physical", _coreResourceFactoryImpl);
        eu.cactosfp7.infrastructuremodels.logicaldc.core.util.CoreResourceFactoryImpl _coreResourceFactoryImpl_1 = new eu.cactosfp7.infrastructuremodels.logicaldc.core.util.CoreResourceFactoryImpl();
        it.put("logical", _coreResourceFactoryImpl_1);
        ExperimentscenarioResourceFactoryImpl _experimentscenarioResourceFactoryImpl = new ExperimentscenarioResourceFactoryImpl();
        it.put("experimentscenario", _experimentscenarioResourceFactoryImpl);
      };
      ObjectExtensions.<Map<String, Object>>operator_doubleArrow(_extensionToFactoryMap, _function_1);
      URI _createURI = URI.createURI((CreateMolproApplicationPerNode.MODELNAME + ".architecturetype"));
      final Resource archTypeResource = resourceSet.getResource(_createURI, true);
      URI _createURI_1 = URI.createURI((CreateMolproApplicationPerNode.MODELNAME + ".logical"));
      final Resource logicalResource = resourceSet.getResource(_createURI_1, true);
      EList<EObject> _contents = logicalResource.getContents();
      Iterable<LogicalDCModel> _filter = Iterables.<LogicalDCModel>filter(_contents, LogicalDCModel.class);
      LogicalDCModel _head = IterableExtensions.<LogicalDCModel>head(_filter);
      EList<EObject> _contents_1 = archTypeResource.getContents();
      Iterable<ArchitectureTypeRepository> _filter_1 = Iterables.<ArchitectureTypeRepository>filter(_contents_1, ArchitectureTypeRepository.class);
      ArchitectureTypeRepository _head_1 = IterableExtensions.<ArchitectureTypeRepository>head(_filter_1);
      CreateMolproApplicationPerNode _createMolproApplicationPerNode = new CreateMolproApplicationPerNode(_head, _head_1);
      _createMolproApplicationPerNode.createAMolproLCCSDInstancePerHypervisor();
      Map<Object, Object> _emptyMap = Collections.<Object, Object>emptyMap();
      logicalResource.save(_emptyMap);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public CreateMolproApplicationPerNode(final LogicalDCModel model, final ArchitectureTypeRepository archRepo) {
    this.logicalDC = model;
    this.architectureTypeRepository = archRepo;
  }
  
  public void createAMolproLCCSDInstancePerHypervisor() {
    this.createFlavour();
    this.createVMImage();
    this.createApplicationTemplate();
    EList<Hypervisor> _hypervisors = this.logicalDC.getHypervisors();
    final Consumer<Hypervisor> _function = (Hypervisor it) -> {
      this.createApplicationInstance(it);
    };
    _hypervisors.forEach(_function);
  }
  
  public boolean createFlavour() {
    boolean _xifexpression = false;
    EList<Flavour> _flavours = this.logicalDC.getFlavours();
    final Function1<Flavour, Boolean> _function = (Flavour it) -> {
      String _name = it.getName();
      return Boolean.valueOf(Objects.equal(_name, "Molpro Generic SingleCore Flavour"));
    };
    Iterable<Flavour> _filter = IterableExtensions.<Flavour>filter(_flavours, _function);
    int _size = IterableExtensions.size(_filter);
    boolean _equals = (_size == 0);
    if (_equals) {
      EList<Flavour> _flavours_1 = this.logicalDC.getFlavours();
      Flavour _createFlavour = CoreFactory.INSTANCE.createFlavour();
      final Procedure1<Flavour> _function_1 = (Flavour it) -> {
        it.setName("Molpro Generic SingleCore Flavour");
        EList<ArchitectureType> _architectureTypes = this.architectureTypeRepository.getArchitectureTypes();
        ArchitectureType _head = IterableExtensions.<ArchitectureType>head(_architectureTypes);
        it.setArchitectureType(_head);
        it.setNumberVirtualCores(1);
        Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
        Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(4.0, _GIGA);
        it.setSizeRam(_valueOf);
        Unit<DataAmount> _GIGA_1 = SI.<DataAmount>GIGA(NonSI.BYTE);
        Amount<DataAmount> _valueOf_1 = Amount.<DataAmount>valueOf(20.0, _GIGA_1);
        it.setSizeStorage(_valueOf_1);
        it.setFlavourRefVMI("XXX");
      };
      Flavour _doubleArrow = ObjectExtensions.<Flavour>operator_doubleArrow(_createFlavour, _function_1);
      _xifexpression = _flavours_1.add(_doubleArrow);
    }
    return _xifexpression;
  }
  
  public boolean createVMImage() {
    boolean _xifexpression = false;
    EList<VirtualDisk> _volumesAndImages = this.logicalDC.getVolumesAndImages();
    Iterable<VMImage> _filter = Iterables.<VMImage>filter(_volumesAndImages, VMImage.class);
    final Function1<VMImage, Boolean> _function = (VMImage it) -> {
      String _name = it.getName();
      return Boolean.valueOf(Objects.equal(_name, "Molpro LCCSD VM Image"));
    };
    Iterable<VMImage> _filter_1 = IterableExtensions.<VMImage>filter(_filter, _function);
    int _size = IterableExtensions.size(_filter_1);
    boolean _equals = (_size == 0);
    if (_equals) {
      EList<VirtualDisk> _volumesAndImages_1 = this.logicalDC.getVolumesAndImages();
      VMImage _createVMImage = CoreFactory.INSTANCE.createVMImage();
      final Procedure1<VMImage> _function_1 = (VMImage it) -> {
        it.setName("Molpro LCCSD VM Image");
        Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
        Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(20.0, _GIGA);
        it.setCapacity(_valueOf);
        Unit<DataAmount> _GIGA_1 = SI.<DataAmount>GIGA(NonSI.BYTE);
        Amount<DataAmount> _valueOf_1 = Amount.<DataAmount>valueOf(5.0, _GIGA_1);
        it.setUsedCapacity(_valueOf_1);
        EList<Hypervisor> _hypervisors = this.logicalDC.getHypervisors();
        Hypervisor _head = IterableExtensions.<Hypervisor>head(_hypervisors);
        ComputeNode _node = _head.getNode();
        EList<StorageSpecification> _storageSpecifications = _node.getStorageSpecifications();
        StorageSpecification _head_1 = IterableExtensions.<StorageSpecification>head(_storageSpecifications);
        it.setStorageLocation(_head_1);
      };
      VMImage _doubleArrow = ObjectExtensions.<VMImage>operator_doubleArrow(_createVMImage, _function_1);
      _xifexpression = _volumesAndImages_1.add(_doubleArrow);
    }
    return _xifexpression;
  }
  
  public boolean createApplicationTemplate() {
    boolean _xblockexpression = false;
    {
      final eu.cactosfp7.infrastructuremodels.physicaldc.core.CoreFactory physiFac = eu.cactosfp7.infrastructuremodels.physicaldc.core.CoreFactory.INSTANCE;
      boolean _xifexpression = false;
      EList<ApplicationTemplate> _applicationTemplates = this.logicalDC.getApplicationTemplates();
      Iterable<GreyBoxApplicationTemplate> _filter = Iterables.<GreyBoxApplicationTemplate>filter(_applicationTemplates, GreyBoxApplicationTemplate.class);
      final Function1<GreyBoxApplicationTemplate, Boolean> _function = (GreyBoxApplicationTemplate it) -> {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, "Molpro LCCSD Application Template"));
      };
      Iterable<GreyBoxApplicationTemplate> _filter_1 = IterableExtensions.<GreyBoxApplicationTemplate>filter(_filter, _function);
      int _size = IterableExtensions.size(_filter_1);
      boolean _equals = (_size == 0);
      if (_equals) {
        EList<ApplicationTemplate> _applicationTemplates_1 = this.logicalDC.getApplicationTemplates();
        GreyBoxApplicationTemplate _createGreyBoxApplicationTemplate = ApplicationFactory.INSTANCE.createGreyBoxApplicationTemplate();
        final Procedure1<GreyBoxApplicationTemplate> _function_1 = (GreyBoxApplicationTemplate it) -> {
          it.setName("Molpro LCCSD Application Template");
          GreyBoxVMImageBehaviour _createGreyBoxVMImageBehaviour = ApplicationFactory.INSTANCE.createGreyBoxVMImageBehaviour();
          final Procedure1<GreyBoxVMImageBehaviour> _function_2 = (GreyBoxVMImageBehaviour it_1) -> {
            EList<VirtualDisk> _volumesAndImages = this.logicalDC.getVolumesAndImages();
            final Function1<VirtualDisk, Boolean> _function_3 = (VirtualDisk it_2) -> {
              String _name = it_2.getName();
              return Boolean.valueOf(Objects.equal(_name, "Molpro LCCSD VM Image"));
            };
            Iterable<VirtualDisk> _filter_2 = IterableExtensions.<VirtualDisk>filter(_volumesAndImages, _function_3);
            VirtualDisk _head = IterableExtensions.<VirtualDisk>head(_filter_2);
            it_1.setVmImage(_head);
            ProcessingUnitSpecification _createProcessingUnitSpecification = physiFac.createProcessingUnitSpecification();
            final Procedure1<ProcessingUnitSpecification> _function_4 = (ProcessingUnitSpecification it_2) -> {
              it_2.setName("Reference Processing Unit Specification");
              Unit<Frequency> _GIGA = SI.<Frequency>GIGA(SI.HERTZ);
              Amount<Frequency> _valueOf = Amount.<Frequency>valueOf(3.2, _GIGA);
              it_2.setFrequency(_valueOf);
              it_2.setNumberOfCores(1);
              EList<ArchitectureType> _architectureTypes = this.architectureTypeRepository.getArchitectureTypes();
              ArchitectureType _head_1 = IterableExtensions.<ArchitectureType>head(_architectureTypes);
              it_2.setArchitectureType(_head_1);
            };
            final ProcessingUnitSpecification refCPU = ObjectExtensions.<ProcessingUnitSpecification>operator_doubleArrow(_createProcessingUnitSpecification, _function_4);
            EList<MonitorableResource> _referenceResourceSpecifications = it_1.getReferenceResourceSpecifications();
            _referenceResourceSpecifications.add(refCPU);
            StorageSpecification _createStorageSpecification = physiFac.createStorageSpecification();
            final Procedure1<StorageSpecification> _function_5 = (StorageSpecification it_2) -> {
              it_2.setName("Reference Storage Specification");
              Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
              Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(220, _GIGA);
              it_2.setSize(_valueOf);
              Amount<Duration> _valueOf_1 = Amount.<Duration>valueOf(0, SI.SECOND);
              it_2.setReadDelay(_valueOf_1);
              Amount<Duration> _valueOf_2 = Amount.<Duration>valueOf(0, SI.SECOND);
              it_2.setWriteDelay(_valueOf_2);
              Bandwidth _createBandwidth = UtilFactory.INSTANCE.createBandwidth();
              final Procedure1<Bandwidth> _function_6 = (Bandwidth it_3) -> {
                Unit<DataRate> _MEGA = SI.<DataRate>MEGA(DataRate.UNIT);
                Amount<DataRate> _valueOf_3 = Amount.<DataRate>valueOf(960.0, _MEGA);
                it_3.setValue(_valueOf_3);
              };
              Bandwidth _doubleArrow = ObjectExtensions.<Bandwidth>operator_doubleArrow(_createBandwidth, _function_6);
              it_2.setReadBandwidth(_doubleArrow);
              Bandwidth _createBandwidth_1 = UtilFactory.INSTANCE.createBandwidth();
              final Procedure1<Bandwidth> _function_7 = (Bandwidth it_3) -> {
                Unit<DataRate> _MEGA = SI.<DataRate>MEGA(DataRate.UNIT);
                Amount<DataRate> _valueOf_3 = Amount.<DataRate>valueOf(960.0, _MEGA);
                it_3.setValue(_valueOf_3);
              };
              Bandwidth _doubleArrow_1 = ObjectExtensions.<Bandwidth>operator_doubleArrow(_createBandwidth_1, _function_7);
              it_2.setWriteBandwidth(_doubleArrow_1);
            };
            final StorageSpecification refStorage = ObjectExtensions.<StorageSpecification>operator_doubleArrow(_createStorageSpecification, _function_5);
            EList<MonitorableResource> _referenceResourceSpecifications_1 = it_1.getReferenceResourceSpecifications();
            _referenceResourceSpecifications_1.add(refStorage);
            EList<WorkloadPhase> _workloadPhases = it_1.getWorkloadPhases();
            WorkloadPhase _createWorkloadPhase = ApplicationFactory.INSTANCE.createWorkloadPhase();
            final Procedure1<WorkloadPhase> _function_6 = (WorkloadPhase it_2) -> {
              it_2.setName("Molpro LCCSD Phase 1");
              EList<ResourceDemand> _resourceDemands = it_2.getResourceDemands();
              ResourceDemand _createResourceDemand = ApplicationFactory.INSTANCE.createResourceDemand();
              final Procedure1<ResourceDemand> _function_7 = (ResourceDemand it_3) -> {
                Unit<Dimensionless> _GIGA = SI.<Dimensionless>GIGA(Dimensionless.UNIT);
                Amount<Dimensionless> _valueOf = Amount.<Dimensionless>valueOf(35374, _GIGA);
                it_3.setAmount(_valueOf);
                it_3.setOnResource(refCPU);
              };
              ResourceDemand _doubleArrow = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand, _function_7);
              _resourceDemands.add(_doubleArrow);
              EList<ResourceDemand> _resourceDemands_1 = it_2.getResourceDemands();
              ResourceDemand _createResourceDemand_1 = ApplicationFactory.INSTANCE.createResourceDemand();
              final Procedure1<ResourceDemand> _function_8 = (ResourceDemand it_3) -> {
                Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
                Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(189, _GIGA);
                it_3.setAmount(_valueOf);
                it_3.setOnResource(refStorage);
              };
              ResourceDemand _doubleArrow_1 = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand_1, _function_8);
              _resourceDemands_1.add(_doubleArrow_1);
            };
            WorkloadPhase _doubleArrow = ObjectExtensions.<WorkloadPhase>operator_doubleArrow(_createWorkloadPhase, _function_6);
            _workloadPhases.add(_doubleArrow);
            EList<WorkloadPhase> _workloadPhases_1 = it_1.getWorkloadPhases();
            WorkloadPhase _createWorkloadPhase_1 = ApplicationFactory.INSTANCE.createWorkloadPhase();
            final Procedure1<WorkloadPhase> _function_7 = (WorkloadPhase it_2) -> {
              it_2.setName("Molpro LCCSD Phase 2");
              EList<ResourceDemand> _resourceDemands = it_2.getResourceDemands();
              ResourceDemand _createResourceDemand = ApplicationFactory.INSTANCE.createResourceDemand();
              final Procedure1<ResourceDemand> _function_8 = (ResourceDemand it_3) -> {
                Unit<Dimensionless> _GIGA = SI.<Dimensionless>GIGA(Dimensionless.UNIT);
                Amount<Dimensionless> _valueOf = Amount.<Dimensionless>valueOf(115, _GIGA);
                it_3.setAmount(_valueOf);
                it_3.setOnResource(refCPU);
              };
              ResourceDemand _doubleArrow_1 = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand, _function_8);
              _resourceDemands.add(_doubleArrow_1);
              EList<ResourceDemand> _resourceDemands_1 = it_2.getResourceDemands();
              ResourceDemand _createResourceDemand_1 = ApplicationFactory.INSTANCE.createResourceDemand();
              final Procedure1<ResourceDemand> _function_9 = (ResourceDemand it_3) -> {
                Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
                Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(234, _GIGA);
                it_3.setAmount(_valueOf);
                it_3.setOnResource(refStorage);
              };
              ResourceDemand _doubleArrow_2 = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand_1, _function_9);
              _resourceDemands_1.add(_doubleArrow_2);
            };
            WorkloadPhase _doubleArrow_1 = ObjectExtensions.<WorkloadPhase>operator_doubleArrow(_createWorkloadPhase_1, _function_7);
            _workloadPhases_1.add(_doubleArrow_1);
            EList<WorkloadPhase> _workloadPhases_2 = it_1.getWorkloadPhases();
            WorkloadPhase _createWorkloadPhase_2 = ApplicationFactory.INSTANCE.createWorkloadPhase();
            final Procedure1<WorkloadPhase> _function_8 = (WorkloadPhase it_2) -> {
              it_2.setName("Molpro LCCSD Phase 3");
              EList<ResourceDemand> _resourceDemands = it_2.getResourceDemands();
              ResourceDemand _createResourceDemand = ApplicationFactory.INSTANCE.createResourceDemand();
              final Procedure1<ResourceDemand> _function_9 = (ResourceDemand it_3) -> {
                Unit<Dimensionless> _GIGA = SI.<Dimensionless>GIGA(Dimensionless.UNIT);
                Amount<Dimensionless> _valueOf = Amount.<Dimensionless>valueOf(28259, _GIGA);
                it_3.setAmount(_valueOf);
                it_3.setOnResource(refCPU);
              };
              ResourceDemand _doubleArrow_2 = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand, _function_9);
              _resourceDemands.add(_doubleArrow_2);
              EList<ResourceDemand> _resourceDemands_1 = it_2.getResourceDemands();
              ResourceDemand _createResourceDemand_1 = ApplicationFactory.INSTANCE.createResourceDemand();
              final Procedure1<ResourceDemand> _function_10 = (ResourceDemand it_3) -> {
                Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
                Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(172, _GIGA);
                it_3.setAmount(_valueOf);
                it_3.setOnResource(refStorage);
              };
              ResourceDemand _doubleArrow_3 = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand_1, _function_10);
              _resourceDemands_1.add(_doubleArrow_3);
            };
            WorkloadPhase _doubleArrow_2 = ObjectExtensions.<WorkloadPhase>operator_doubleArrow(_createWorkloadPhase_2, _function_8);
            _workloadPhases_2.add(_doubleArrow_2);
            EList<WorkloadPhase> _workloadPhases_3 = it_1.getWorkloadPhases();
            WorkloadPhase _createWorkloadPhase_3 = ApplicationFactory.INSTANCE.createWorkloadPhase();
            final Procedure1<WorkloadPhase> _function_9 = (WorkloadPhase it_2) -> {
              it_2.setName("Molpro LCCSD Phase 4");
              EList<ResourceDemand> _resourceDemands = it_2.getResourceDemands();
              ResourceDemand _createResourceDemand = ApplicationFactory.INSTANCE.createResourceDemand();
              final Procedure1<ResourceDemand> _function_10 = (ResourceDemand it_3) -> {
                Unit<Dimensionless> _GIGA = SI.<Dimensionless>GIGA(Dimensionless.UNIT);
                Amount<Dimensionless> _valueOf = Amount.<Dimensionless>valueOf(2212, _GIGA);
                it_3.setAmount(_valueOf);
                it_3.setOnResource(refCPU);
              };
              ResourceDemand _doubleArrow_3 = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand, _function_10);
              _resourceDemands.add(_doubleArrow_3);
              EList<ResourceDemand> _resourceDemands_1 = it_2.getResourceDemands();
              ResourceDemand _createResourceDemand_1 = ApplicationFactory.INSTANCE.createResourceDemand();
              final Procedure1<ResourceDemand> _function_11 = (ResourceDemand it_3) -> {
                Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
                Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(422, _GIGA);
                it_3.setAmount(_valueOf);
                it_3.setOnResource(refStorage);
              };
              ResourceDemand _doubleArrow_4 = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand_1, _function_11);
              _resourceDemands_1.add(_doubleArrow_4);
            };
            WorkloadPhase _doubleArrow_3 = ObjectExtensions.<WorkloadPhase>operator_doubleArrow(_createWorkloadPhase_3, _function_9);
            _workloadPhases_3.add(_doubleArrow_3);
          };
          GreyBoxVMImageBehaviour _doubleArrow = ObjectExtensions.<GreyBoxVMImageBehaviour>operator_doubleArrow(_createGreyBoxVMImageBehaviour, _function_2);
          it.setGreyBoxVMImageBehaviour(_doubleArrow);
        };
        GreyBoxApplicationTemplate _doubleArrow = ObjectExtensions.<GreyBoxApplicationTemplate>operator_doubleArrow(_createGreyBoxApplicationTemplate, _function_1);
        _xifexpression = _applicationTemplates_1.add(_doubleArrow);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public boolean createApplicationInstance(final Hypervisor forHypervisor) {
    boolean _xblockexpression = false;
    {
      EList<ApplicationTemplate> _applicationTemplates = this.logicalDC.getApplicationTemplates();
      Iterable<GreyBoxApplicationTemplate> _filter = Iterables.<GreyBoxApplicationTemplate>filter(_applicationTemplates, GreyBoxApplicationTemplate.class);
      final Function1<GreyBoxApplicationTemplate, Boolean> _function = (GreyBoxApplicationTemplate it) -> {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, "Molpro LCCSD Application Template"));
      };
      Iterable<GreyBoxApplicationTemplate> _filter_1 = IterableExtensions.<GreyBoxApplicationTemplate>filter(_filter, _function);
      final GreyBoxApplicationTemplate appTemplate = IterableExtensions.<GreyBoxApplicationTemplate>head(_filter_1);
      boolean _xifexpression = false;
      EList<VirtualMachine> _virtualMachines = forHypervisor.getVirtualMachines();
      final Function1<VirtualMachine, Boolean> _function_1 = (VirtualMachine it) -> {
        VMBehaviour _runtimeApplicationModel = it.getRuntimeApplicationModel();
        VMImageBehaviour _vmImageBehaviour = _runtimeApplicationModel.getVmImageBehaviour();
        String _id = _vmImageBehaviour.getId();
        GreyBoxVMImageBehaviour _greyBoxVMImageBehaviour = appTemplate.getGreyBoxVMImageBehaviour();
        String _id_1 = _greyBoxVMImageBehaviour.getId();
        return Boolean.valueOf(Objects.equal(_id, _id_1));
      };
      Iterable<VirtualMachine> _filter_2 = IterableExtensions.<VirtualMachine>filter(_virtualMachines, _function_1);
      int _size = IterableExtensions.size(_filter_2);
      boolean _equals = (_size == 0);
      if (_equals) {
        boolean _xblockexpression_1 = false;
        {
          VirtualMachine _createVirtualMachine = CoreFactory.INSTANCE.createVirtualMachine();
          final Procedure1<VirtualMachine> _function_2 = (VirtualMachine vm) -> {
            vm.setName("Molpro LCCSD Virtual Machine");
            EList<VirtualMemory> _virtualMemoryUnits = vm.getVirtualMemoryUnits();
            VirtualMemory _createVirtualMemory = CoreFactory.INSTANCE.createVirtualMemory();
            final Procedure1<VirtualMemory> _function_3 = (VirtualMemory it) -> {
              Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
              Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(8, _GIGA);
              it.setProvisioned(_valueOf);
            };
            VirtualMemory _doubleArrow = ObjectExtensions.<VirtualMemory>operator_doubleArrow(_createVirtualMemory, _function_3);
            _virtualMemoryUnits.add(_doubleArrow);
            VirtualDisk _createVirtualDisk = CoreFactory.INSTANCE.createVirtualDisk();
            final Procedure1<VirtualDisk> _function_4 = (VirtualDisk it) -> {
              ComputeNode _node = forHypervisor.getNode();
              String _name = _node.getName();
              String _plus = ("Disk Overlay for VM on Hypervisor of " + _name);
              it.setName(_plus);
              VirtualDisk _rootVolume = forHypervisor.getRootVolume();
              StorageSpecification _storageLocation = _rootVolume.getStorageLocation();
              it.setStorageLocation(_storageLocation);
              Unit<DataAmount> _GIGA = SI.<DataAmount>GIGA(NonSI.BYTE);
              Amount<DataAmount> _valueOf = Amount.<DataAmount>valueOf(20, _GIGA);
              it.setCapacity(_valueOf);
              Unit<DataAmount> _GIGA_1 = SI.<DataAmount>GIGA(NonSI.BYTE);
              Amount<DataAmount> _valueOf_1 = Amount.<DataAmount>valueOf(20, _GIGA_1);
              it.setUsedCapacity(_valueOf_1);
              GreyBoxVMImageBehaviour _greyBoxVMImageBehaviour = appTemplate.getGreyBoxVMImageBehaviour();
              VirtualDisk _vmImage = _greyBoxVMImageBehaviour.getVmImage();
              EList<VirtualDisk> _deltaOverlay = _vmImage.getDeltaOverlay();
              _deltaOverlay.add(it);
            };
            final VirtualDisk vdisk = ObjectExtensions.<VirtualDisk>operator_doubleArrow(_createVirtualDisk, _function_4);
            VMImageInstance _createVMImageInstance = CoreFactory.INSTANCE.createVMImageInstance();
            final Procedure1<VMImageInstance> _function_5 = (VMImageInstance it) -> {
              it.setRootDisk(vdisk);
            };
            VMImageInstance _doubleArrow_1 = ObjectExtensions.<VMImageInstance>operator_doubleArrow(_createVMImageInstance, _function_5);
            vm.setVMImageInstance(_doubleArrow_1);
            VirtualProcessingUnit _createVirtualProcessingUnit = CoreFactory.INSTANCE.createVirtualProcessingUnit();
            final Procedure1<VirtualProcessingUnit> _function_6 = (VirtualProcessingUnit it) -> {
              it.setName("Generic SingleCore VCPU");
              EList<ArchitectureType> _architectureTypes = this.architectureTypeRepository.getArchitectureTypes();
              ArchitectureType _head = IterableExtensions.<ArchitectureType>head(_architectureTypes);
              it.setArchitectureType(_head);
              it.setVirtualCores(1);
              EList<VirtualProcessingUnit> _virtualProcessingUnits = vm.getVirtualProcessingUnits();
              _virtualProcessingUnits.add(it);
            };
            final VirtualProcessingUnit vcpu = ObjectExtensions.<VirtualProcessingUnit>operator_doubleArrow(_createVirtualProcessingUnit, _function_6);
            GreyBoxVMBehaviour _createGreyBoxVMBehaviour = ApplicationFactory.INSTANCE.createGreyBoxVMBehaviour();
            final Procedure1<GreyBoxVMBehaviour> _function_7 = (GreyBoxVMBehaviour vmb) -> {
              String _name = vm.getName();
              String _plus = ("GreyBoxVMBehaviour of " + _name);
              vmb.setName(_plus);
              GreyBoxVMImageBehaviour _greyBoxVMImageBehaviour = appTemplate.getGreyBoxVMImageBehaviour();
              vmb.setVmImageBehaviour(_greyBoxVMImageBehaviour);
              GreyBoxVMImageBehaviour _greyBoxVMImageBehaviour_1 = appTemplate.getGreyBoxVMImageBehaviour();
              EList<WorkloadPhase> _workloadPhases = _greyBoxVMImageBehaviour_1.getWorkloadPhases();
              final Consumer<WorkloadPhase> _function_8 = (WorkloadPhase templatePhase) -> {
                EList<WorkloadPhase> _workloadPhases_1 = vmb.getWorkloadPhases();
                WorkloadPhase _createWorkloadPhase = ApplicationFactory.INSTANCE.createWorkloadPhase();
                final Procedure1<WorkloadPhase> _function_9 = (WorkloadPhase newPhase) -> {
                  String _name_1 = templatePhase.getName();
                  newPhase.setName(_name_1);
                  EList<ResourceDemand> _resourceDemands = templatePhase.getResourceDemands();
                  final Consumer<ResourceDemand> _function_10 = (ResourceDemand templateDemand) -> {
                    EList<ResourceDemand> _resourceDemands_1 = newPhase.getResourceDemands();
                    ResourceDemand _createResourceDemand = ApplicationFactory.INSTANCE.createResourceDemand();
                    final Procedure1<ResourceDemand> _function_11 = (ResourceDemand newDemand) -> {
                      MonitorableResource _onResource = templateDemand.getOnResource();
                      newDemand.setOnResource(_onResource);
                      Amount _amount = templateDemand.getAmount();
                      newDemand.setAmount(_amount);
                    };
                    ResourceDemand _doubleArrow_2 = ObjectExtensions.<ResourceDemand>operator_doubleArrow(_createResourceDemand, _function_11);
                    _resourceDemands_1.add(_doubleArrow_2);
                  };
                  _resourceDemands.forEach(_function_10);
                };
                WorkloadPhase _doubleArrow_2 = ObjectExtensions.<WorkloadPhase>operator_doubleArrow(_createWorkloadPhase, _function_9);
                _workloadPhases_1.add(_doubleArrow_2);
              };
              _workloadPhases.forEach(_function_8);
            };
            GreyBoxVMBehaviour _doubleArrow_2 = ObjectExtensions.<GreyBoxVMBehaviour>operator_doubleArrow(_createGreyBoxVMBehaviour, _function_7);
            vm.setRuntimeApplicationModel(_doubleArrow_2);
            EList<VirtualMachine> _virtualMachines_1 = forHypervisor.getVirtualMachines();
            _virtualMachines_1.add(vm);
          };
          final VirtualMachine newVM = ObjectExtensions.<VirtualMachine>operator_doubleArrow(_createVirtualMachine, _function_2);
          EList<ApplicationInstance> _applicationInstances = this.logicalDC.getApplicationInstances();
          GreyBoxApplicationInstance _createGreyBoxApplicationInstance = ApplicationFactory.INSTANCE.createGreyBoxApplicationInstance();
          final Procedure1<GreyBoxApplicationInstance> _function_3 = (GreyBoxApplicationInstance it) -> {
            it.setVirtualMachine(newVM);
            it.setApplicationTemplate(appTemplate);
          };
          GreyBoxApplicationInstance _doubleArrow = ObjectExtensions.<GreyBoxApplicationInstance>operator_doubleArrow(_createGreyBoxApplicationInstance, _function_3);
          _xblockexpression_1 = _applicationInstances.add(_doubleArrow);
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
}
