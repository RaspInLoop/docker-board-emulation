package org.raspinloop.emulator.proxyserver.simulation;

import java.util.EnumSet;
import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableStateMachine
@Slf4j
public class StateMachineConfig
        extends EnumStateMachineConfigurerAdapter<SimulationStates, SimulationEvents> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<SimulationStates, SimulationEvents> config)
            throws Exception {
        config
            .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<SimulationStates, SimulationEvents> states)
            throws Exception {
        states
            .withStates()
                .initial(SimulationStates.STOPPED)
                    .states(EnumSet.allOf(SimulationStates.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SimulationStates, SimulationEvents> transitions)
            throws Exception {
        transitions
                 .withExternal().source(SimulationStates.STOPPED).target(SimulationStates.WAITING).event(SimulationEvents.START)             
           .and().withExternal().source(SimulationStates.WAITING).target(SimulationStates.CONFIGURING).event(SimulationEvents.FMU_INSTANCIATE)
           .and().withExternal().source(SimulationStates.CONFIGURING).target(SimulationStates.UPDATING).event(SimulationEvents.FMU_SET_VALUE)
           .and().withExternal().source(SimulationStates.UPDATING).target(SimulationStates.UPDATING).event(SimulationEvents.FMU_SET_VALUE)
           .and().withExternal().source(SimulationStates.QUERYING).target(SimulationStates.UPDATING).event(SimulationEvents.FMU_SET_VALUE)
           .and().withExternal().source(SimulationStates.UPDATING).target(SimulationStates.EMULATING).event(SimulationEvents.FMU_DO_STEP)
           .and().withExternal().source(SimulationStates.EMULATING).target(SimulationStates.QUERYING).event(SimulationEvents.FMU_GET_VALUE)
           .and().withExternal().source(SimulationStates.QUERYING).target(SimulationStates.QUERYING).event(SimulationEvents.FMU_GET_VALUE)
           .and().withExternal().source(SimulationStates.WAITING).target(SimulationStates.STOPPING).event(SimulationEvents.STOP)
           .and().withExternal().source(SimulationStates.CONFIGURING).target(SimulationStates.STOPPING).event(SimulationEvents.STOP)
           .and().withExternal().source(SimulationStates.UPDATING).target(SimulationStates.STOPPING).event(SimulationEvents.STOP)
           .and().withExternal().source(SimulationStates.EMULATING).target(SimulationStates.STOPPING).event(SimulationEvents.STOP)
           .and().withExternal().source(SimulationStates.QUERYING).target(SimulationStates.STOPPING).event(SimulationEvents.STOP)
           .and().withExternal().source(SimulationStates.STOPPING).target(SimulationStates.STOPPING).event(SimulationEvents.FMU_GET_VALUE)
           .and().withExternal().source(SimulationStates.STOPPING).target(SimulationStates.STOPPED).event(SimulationEvents.FMU_TERMINATE_RESULT);
    }

    @Bean
    public StateMachineListener<SimulationStates, SimulationEvents> listener() {
        return new StateMachineListenerAdapter<SimulationStates, SimulationEvents>() {
            @Override
            public void stateChanged(State<SimulationStates, SimulationEvents> from, State<SimulationStates, SimulationEvents> to) {
               log.info("FSM: {} --> {}", from == null ? "NOT SET":from.getId(), to == null ? "NOT SET": to.getId());
            }
        };
    }
}
