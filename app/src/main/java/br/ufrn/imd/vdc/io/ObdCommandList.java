package br.ufrn.imd.vdc.io;


import com.github.pires.obd.commands.ObdCommand;
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
    private List<ObdCommand> commands;

    private ObdCommandList() {
        fillCommandsList();
    }

    public static ObdCommandList getInstance() {
        return instance;
    }

    public List<ObdCommand> getCommands() {
        return commands;
    }

    private void fillCommandsList() {
        commands = new ArrayList<>();

        // Control
        commands.add(new ModuleVoltageCommand());
        commands.add(new EquivalentRatioCommand());
        commands.add(new DistanceMILOnCommand());
        commands.add(new DtcNumberCommand());
        commands.add(new TimingAdvanceCommand());
        commands.add(new TroubleCodesCommand());
        commands.add(new VinCommand());

        // Engine
        commands.add(new LoadCommand());
        commands.add(new RPMCommand());
        commands.add(new RuntimeCommand());
        commands.add(new MassAirFlowCommand());
        commands.add(new ThrottlePositionCommand());

        // Fuel
        commands.add(new FindFuelTypeCommand());
        commands.add(new ConsumptionRateCommand());
        // commands.add(new AverageFuelEconomyObdCommand());
        //commands.add(new FuelEconomyCommand());
        commands.add(new FuelLevelCommand());
        // commands.add(new FuelEconomyMAPObdCommand());
        // commands.add(new FuelEconomyCommandedMAPObdCommand());
        commands.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1));
        commands.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2));
        commands.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1));
        commands.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2));
        commands.add(new AirFuelRatioCommand());
        commands.add(new WidebandAirFuelRatioCommand());
        commands.add(new OilTempCommand());

        // Pressure
        commands.add(new BarometricPressureCommand());
        commands.add(new FuelPressureCommand());
        commands.add(new FuelRailPressureCommand());
        commands.add(new IntakeManifoldPressureCommand());

        // Temperature
        commands.add(new AirIntakeTemperatureCommand());
        commands.add(new AmbientAirTemperatureCommand());
        commands.add(new EngineCoolantTemperatureCommand());

        // Misc
        commands.add(new SpeedCommand());
    }
}
