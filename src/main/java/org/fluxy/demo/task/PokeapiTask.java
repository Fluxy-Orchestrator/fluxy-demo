package org.fluxy.demo.task;

import lombok.extern.slf4j.Slf4j;
import org.fluxy.core.model.ExecutionContext;
import org.fluxy.core.model.TaskResult;
import org.fluxy.spring.annotation.Task;
import org.fluxy.starter.support.AbstractFluxyTask;

@Task(name = "pokeapi", description = "Fetch data from PokeAPI", version = 1)
@Slf4j
public class PokeapiTask extends AbstractFluxyTask {

    @Override
    public TaskResult execute(ExecutionContext executionContext) {
        log.info("Pokeapi task processed");
        return TaskResult.SUCCESS;
    }
}
