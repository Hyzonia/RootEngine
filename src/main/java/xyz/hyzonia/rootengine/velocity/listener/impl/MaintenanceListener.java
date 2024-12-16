package xyz.hyzonia.rootengine.velocity.listener.impl;

import eu.kennytv.maintenance.api.event.MaintenanceChangedEvent;
import eu.kennytv.maintenance.api.event.manager.EventListener;
import xyz.hyzonia.rootengine.common.DiscordWebhookSender;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;

import java.io.IOException;

public class MaintenanceListener extends EventListener<MaintenanceChangedEvent> {
    public MaintenanceListener() {
        super();
        VelocityEngine.LOGGER.info("Registered listener: {}", this.getClass().getName());
    }

    @Override
    public void onEvent(MaintenanceChangedEvent event) {
        if (event.isMaintenance()) {
            try {
                new DiscordWebhookSender(
                        VelocityEngine.CONFIG.getMaintenanceWebhook()
                )
                        .sendMessage(
                                """
                                        {
                                          "content": "MESSAGE"
                                        }
                                        """.replaceAll(
                                                "MESSAGE",
                                        VelocityEngine.CONFIG.getMaintenanceMessage()
                                )
                        );
                // TODO: Maintenance right now doesn't support all parameters for a maintenance entry, maybe hook into the database?
            } catch (IOException e) {
                VelocityEngine.LOGGER.error("Error while posting maintenance status!", e);
            }
        }
    }
}
