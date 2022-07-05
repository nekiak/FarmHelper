package com.jelly.farmhelper;

import com.jelly.farmhelper.config.FarmHelperConfig;
import com.jelly.farmhelper.config.interfaces.MiscConfig;
import com.jelly.farmhelper.features.*;
import com.jelly.farmhelper.gui.MenuGUI;
import com.jelly.farmhelper.gui.Render;
import com.jelly.farmhelper.macros.MacroHandler;
import com.jelly.farmhelper.remote.RemoteControlHandler;
import com.jelly.farmhelper.utils.KeyBindUtils;
import com.jelly.farmhelper.world.GameState;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


@Mod(modid = FarmHelper.MODID, name = FarmHelper.NAME, version = FarmHelper.VERSION)
public class FarmHelper {
    public static final String MODID = "farmhelper";
    public static final String NAME = "Farm Helper";
    public static final String VERSION = "4.2.2";
    // the actual mod version from gradle properties, should match with VERSION
    public static String MODVERSION = "-1";
    public static String BOTVERSION = "-1";
    public static int tickCount = 0;
    public static boolean openedGUI = false;
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static GameState gameState;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        setVersions();
        FarmHelperConfig.init();
        KeyBindUtils.setup();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Render());
        MinecraftForge.EVENT_BUS.register(new MenuGUI());
        MinecraftForge.EVENT_BUS.register(new MacroHandler());
        MinecraftForge.EVENT_BUS.register(new Failsafe());
        MinecraftForge.EVENT_BUS.register(new Antistuck());
        MinecraftForge.EVENT_BUS.register(new Autosell());
        MinecraftForge.EVENT_BUS.register(new Scheduler());
        MinecraftForge.EVENT_BUS.register(new AutoReconnect());
        MinecraftForge.EVENT_BUS.register(new AutoCookie());
        MinecraftForge.EVENT_BUS.register(new AutoPot());
        MinecraftForge.EVENT_BUS.register(new BanwaveChecker());
        MinecraftForge.EVENT_BUS.register(new RemoteControlHandler());
        gameState = new GameState();
    }

    @SubscribeEvent
    public void OnKeyPress(InputEvent.KeyInputEvent event) {
        if (KeyBindUtils.customKeyBinds[0].isPressed()) {
            openedGUI = true;
            mc.displayGuiScreen(new MenuGUI());
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public final void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (mc.thePlayer != null && mc.theWorld != null)
            gameState.update();
        tickCount += 1;
        tickCount %= 20;
    }

    @SneakyThrows
    public static void setVersions() {
        Class clazz = FarmHelper.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) return;

        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                "/META-INF/MANIFEST.MF";
        Manifest manifest = new Manifest(new URL(manifestPath).openStream());
        Attributes attr = manifest.getMainAttributes();
        MODVERSION = attr.getValue("modversion");
        BOTVERSION = attr.getValue("botversion");
        Display.setTitle(FarmHelper.NAME + " " + MODVERSION + " | Bing Chilling");
    }
}
