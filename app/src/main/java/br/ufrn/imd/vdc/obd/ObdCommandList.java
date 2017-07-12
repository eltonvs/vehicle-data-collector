package br.ufrn.imd.vdc.obd;


import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.EquivalentRatioCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.TimingAdvanceCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
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
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.FuelTrim;

import java.util.ArrayList;
import java.util.List;


public class ObdCommandList {
    private static final ObdCommandList instance = new ObdCommandList();
    private List<ICommand> commands;

    private ObdCommandList() {
        fillCommandsList();
    }

    public static ObdCommandList getInstance() {
        return instance;
    }

    private void fillCommandsList() {
        // Default Commands
        commands = new ArrayList<>();

        // Control
        commands.add(new ObdCommandAdapter(new ModuleVoltageCommand()));
        commands.add(new ObdCommandAdapter(new EquivalentRatioCommand()));
        commands.add(new ObdCommandAdapter(new DistanceMILOnCommand()));
        commands.add(new ObdCommandAdapter(new DtcNumberCommand()));
        commands.add(new ObdCommandAdapter(new TimingAdvanceCommand()));
        commands.add(new ObdCommandAdapter(new TroubleCodesCommand()));
        commands.add(new ObdCommandAdapter(new VinCommand()));

        // Engine
        commands.add(new ObdCommandAdapter(new LoadCommand()));
        commands.add(new ObdCommandAdapter(new RPMCommand()));
        commands.add(new ObdCommandAdapter(new RuntimeCommand()));
        commands.add(new ObdCommandAdapter(new MassAirFlowCommand()));
        commands.add(new ObdCommandAdapter(new ThrottlePositionCommand()));

        // Fuel
        commands.add(new ObdCommandAdapter(new FindFuelTypeCommand()));
        commands.add(new ObdCommandAdapter(new ConsumptionRateCommand()));
        // commands.add(new ObdCommandAdapter(new AverageFuelEconomyObdCommand()));
        // commands.add(new ObdCommandAdapter(new FuelEconomyCommand()));
        commands.add(new ObdCommandAdapter(new FuelLevelCommand()));
        // commands.add(new ObdCommandAdapter(new FuelEconomyMAPObdCommand()));
        // commands.add(new ObdCommandAdapter(new FuelEconomyCommandedMAPObdCommand()));
        commands.add(new ObdCommandAdapter(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1)));
        commands.add(new ObdCommandAdapter(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2)));
        commands.add(new ObdCommandAdapter(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1)));
        commands.add(new ObdCommandAdapter(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2)));
        commands.add(new ObdCommandAdapter(new AirFuelRatioCommand()));
        commands.add(new ObdCommandAdapter(new WidebandAirFuelRatioCommand()));
        commands.add(new ObdCommandAdapter(new OilTempCommand()));

        // Pressure
        commands.add(new ObdCommandAdapter(new BarometricPressureCommand()));
        commands.add(new ObdCommandAdapter(new FuelPressureCommand()));
        commands.add(new ObdCommandAdapter(new FuelRailPressureCommand()));
        commands.add(new ObdCommandAdapter(new IntakeManifoldPressureCommand()));

        // Temperature
        commands.add(new ObdCommandAdapter(new AirIntakeTemperatureCommand()));
        commands.add(new ObdCommandAdapter(new AmbientAirTemperatureCommand()));
        commands.add(new ObdCommandAdapter(new EngineCoolantTemperatureCommand()));

        // Misc
        commands.add(new ObdCommandAdapter(new SpeedCommand()));
    }

    public List<ICommand> getCommands() {
        return commands;
    }
}
