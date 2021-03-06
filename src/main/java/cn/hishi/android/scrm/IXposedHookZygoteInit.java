package cn.hishi.android.scrm;

import cn.hishi.android.scrm.callbacks.XC_InitZygote;
import cn.hishi.android.scrm.callbacks.XCallback;

/**
 * Hook the initialization of Zygote process(es), from which all the apps are forked.
 *
 * <p>Implement this interface in your module's main class in order to be notified when Android is
 * starting up. In {@link IXposedHookZygoteInit}, you can modify objects and place hooks that should
 * be applied for every app. Only the Android framework/system classes are available at that point
 * in time. Use {@code null} as class loader for {@link XposedHelpers#findAndHookMethod(String, ClassLoader, String, Object...)}
 * and its variants.
 *
 * <p>If you want to hook one/multiple specific apps, use {@link IXposedHookLoadPackage} instead.
 */
public interface IXposedHookZygoteInit extends IXposedMod {
	/**
	 * Called very early during startup of Zygote.
	 * @param startupParam Details about the module itself and the started process.
	 * @throws Throwable everything is caught, but will prevent further initialization of the module.
	 */
	void initZygote(StartupParam startupParam) throws Throwable;

	/** Data holder for {@link #initZygote}. */
	final class StartupParam extends XCallback.Param {
		/*package*/ StartupParam() {}

        /**
         * @param callbacks
         * @hide
         */
        public StartupParam(XposedBridge.CopyOnWriteSortedSet<? extends XCallback> callbacks) {
            super(callbacks);
        }

		/** The path to the module's APK. */
		public String modulePath;

		/**
		 * Always {@code true} on 32-bit ROMs. On 64-bit, it's only {@code true} for the primary
		 * process that starts the system_server.
		 */
		public boolean startsSystemServer;
	}

    /**
     * @hide
     */
    final class Wrapper extends XC_InitZygote {
        private final IXposedHookZygoteInit instance;
        private final StartupParam startupParam;

        public Wrapper(IXposedHookZygoteInit instance, StartupParam startupParam) {
            this.instance = instance;
            this.startupParam = startupParam;
        }

        @Override
        public void initZygote(StartupParam startupParam) throws Throwable {
            // NOTE: parameter startupParam not used
            // cause startupParam info is generated and saved along with instance here
            instance.initZygote(this.startupParam);
        }

		@Override
		public String getApkPath() {
			return startupParam.modulePath;
		}
	}
}
