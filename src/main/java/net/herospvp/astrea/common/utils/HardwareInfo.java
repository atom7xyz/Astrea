package net.herospvp.astrea.common.utils;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.text.DecimalFormat;

public class HardwareInfo {

    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;

    private final CentralProcessor cpu;
    private final CentralProcessor.ProcessorIdentifier cpuIdentifier;

    private final GlobalMemory mem;

    private long[][] oldProcTicks;

    private DecimalFormat decimalFormat;

    public HardwareInfo() {
        systemInfo = new SystemInfo();
        hardware = systemInfo.getHardware();
        cpu = hardware.getProcessor();
        cpuIdentifier = cpu.getProcessorIdentifier();
        mem = hardware.getMemory();
        oldProcTicks = new long[cpu.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
        decimalFormat = new DecimalFormat("0.00");
    }

    //
    // CPUs
    //
    public int getCPUCores() {
        return cpu.getPhysicalProcessorCount();
    }

    public int getCPUThreads() {
        return cpu.getLogicalProcessorCount();
    }

    public double getCPUMaxFrequency() {
        return getRounded(cpu.getMaxFreq() / 1000000000.0);
    }

    public double getCPUFrequency() {
        return getRounded(cpu.getCurrentFreq()[0] / 1000000000.0);
    }

    public String getCPUName() {
        return cpuIdentifier.getName();
    }

    public double[] getCPUUsage() {
        double[] value = cpu.getProcessorCpuLoadBetweenTicks(oldProcTicks);
        oldProcTicks = cpu.getProcessorCpuLoadTicks();
        return value;
    }

    public double getCPUUsageTotal() {
        double[] value = getCPUUsage();
        double sum = 0;
        for (double v : value) {
            sum += v;
        }
        return getRounded(sum / value.length) * 100.0;
    }

    //
    // Memory
    //
    public long getRAMTotal() {
        return mem.getTotal() / 1000000;
    }

    public long getRAMAvailable() {
        return mem.getAvailable() / 1000000;
    }

    private <G> double getRounded(G generic) {
        try {
            return (double) decimalFormat.parse(decimalFormat.format(generic));
        } catch (Exception e) {
            return 0;
        }
    }

}
