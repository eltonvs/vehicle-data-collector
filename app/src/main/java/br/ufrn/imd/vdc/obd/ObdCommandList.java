package br.ufrn.imd.vdc.obd;


import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.control.DistanceSinceCCCommand;
import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.EquivalentRatioCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand;
import com.github.pires.obd.commands.control.PermanentTroubleCodesCommand;
import com.github.pires.obd.commands.control.TimingAdvanceCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.fuel.FuelTrimCommand;
import com.github.pires.obd.commands.fuel.WidebandAirFuelRatioCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.pressure.FuelRailPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_01_20;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_21_40;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_41_60;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.FuelTrim;
import com.github.pires.obd.enums.ObdProtocols;


public class ObdCommandList {
    private static final ObdCommandList instance = new ObdCommandList();

    private ObdCommandList() {
    }

    public static ObdCommandList getInstance() {
        return instance;
    }

    public ICommand setupDevice() {
        return setupDevice(ObdProtocols.AUTO);
    }

    public ICommand setupDevice(ObdProtocols protocol) {
        // Setup Commands
        ObdCommandGroup setupCommands = new ObdCommandGroup();

        setupCommands.add(new ObdCommandAdapter(new ObdResetCommand()));
        setupCommands.add(new ObdCommandAdapter(new EchoOffCommand()));
        setupCommands.add(new ObdCommandAdapter(new LineFeedOffCommand()));
        setupCommands.add(new ObdCommandAdapter(new TimeoutCommand(62)));
        setupCommands.add(new ObdCommandAdapter(new SelectProtocolCommand(protocol)));

        return setupCommands;
    }

    public ICommand vehicleInformation() {
        // Vehicle Information Commands
        ObdCommandGroup vehicleInformation = new ObdCommandGroup();

        vehicleInformation.add(new ObdCommandAdapter(new TroubleCodesCommand()));
        vehicleInformation.add(new ObdCommandAdapter(new VinCommand()));

        // Protocol
        // TODO: Map available commands, then create dynamic data list based on that
        vehicleInformation.add(new ObdCommandAdapter(new AvailablePidsCommand_01_20()));
        vehicleInformation.add(new ObdCommandAdapter(new AvailablePidsCommand_21_40()));
        vehicleInformation.add(new ObdCommandAdapter(new AvailablePidsCommand_41_60()));

        return vehicleInformation;
    }

    public ICommand dynamicData() {
        // Dynamic Data Commands
        ObdCommandGroup dynamicData = new ObdCommandGroup();

        // Misc
        dynamicData.add(new ObdCommandAdapter(new SpeedCommand()));

        // Control
        dynamicData.add(new ObdCommandAdapter(new DistanceMILOnCommand()));
        dynamicData.add(new ObdCommandAdapter(new DistanceSinceCCCommand()));
        dynamicData.add(new ObdCommandAdapter(new DtcNumberCommand()));
        dynamicData.add(new ObdCommandAdapter(new EquivalentRatioCommand()));
        dynamicData.add(new ObdCommandAdapter(new ModuleVoltageCommand()));
        dynamicData.add(new ObdCommandAdapter(new TimingAdvanceCommand()));
        dynamicData.add(new ObdCommandAdapter(new PendingTroubleCodesCommand()));
        dynamicData.add(new ObdCommandAdapter(new PermanentTroubleCodesCommand()));

        // Engine
        dynamicData.add(new ObdCommandAdapter(new AbsoluteLoadCommand()));
        dynamicData.add(new ObdCommandAdapter(new LoadCommand()));
        dynamicData.add(new ObdCommandAdapter(new MassAirFlowCommand()));
        dynamicData.add(new ObdCommandAdapter(new OilTempCommand()));
        dynamicData.add(new ObdCommandAdapter(new RPMCommand()));
        dynamicData.add(new ObdCommandAdapter(new RuntimeCommand()));
        dynamicData.add(new ObdCommandAdapter(new ThrottlePositionCommand()));

        // Fuel
        dynamicData.add(new ObdCommandAdapter(new AirFuelRatioCommand()));
        dynamicData.add(new ObdCommandAdapter(new ConsumptionRateCommand()));
        dynamicData.add(new ObdCommandAdapter(new FindFuelTypeCommand()));
        dynamicData.add(new ObdCommandAdapter(new FuelLevelCommand()));
        dynamicData.add(new ObdCommandAdapter(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1)));
        dynamicData.add(new ObdCommandAdapter(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2)));
        dynamicData.add(new ObdCommandAdapter(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1)));
        dynamicData.add(new ObdCommandAdapter(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2)));
        dynamicData.add(new ObdCommandAdapter(new WidebandAirFuelRatioCommand()));

        // Pressure
        dynamicData.add(new ObdCommandAdapter(new BarometricPressureCommand()));
        dynamicData.add(new ObdCommandAdapter(new FuelPressureCommand()));
        dynamicData.add(new ObdCommandAdapter(new FuelRailPressureCommand()));
        dynamicData.add(new ObdCommandAdapter(new IntakeManifoldPressureCommand()));

        // Temperature
        dynamicData.add(new ObdCommandAdapter(new AirIntakeTemperatureCommand()));
        dynamicData.add(new ObdCommandAdapter(new AmbientAirTemperatureCommand()));
        dynamicData.add(new ObdCommandAdapter(new EngineCoolantTemperatureCommand()));

        return dynamicData;
    }
}
