#ifndef KONAN_LIBKSSH2_H
#define KONAN_LIBKSSH2_H
#ifdef __cplusplus
extern "C" {
#endif
#ifdef __cplusplus
typedef bool            libkssh2_KBoolean;
#else
typedef _Bool           libkssh2_KBoolean;
#endif
typedef unsigned short     libkssh2_KChar;
typedef signed char        libkssh2_KByte;
typedef short              libkssh2_KShort;
typedef int                libkssh2_KInt;
typedef long long          libkssh2_KLong;
typedef unsigned char      libkssh2_KUByte;
typedef unsigned short     libkssh2_KUShort;
typedef unsigned int       libkssh2_KUInt;
typedef unsigned long long libkssh2_KULong;
typedef float              libkssh2_KFloat;
typedef double             libkssh2_KDouble;
typedef float __attribute__ ((__vector_size__ (16))) libkssh2_KVector128;
typedef void*              libkssh2_KNativePtr;
struct libkssh2_KType;
typedef struct libkssh2_KType libkssh2_KType;

typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Byte;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Short;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Int;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Long;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Float;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Double;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Char;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Boolean;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Unit;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_UByte;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_UShort;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_UInt;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_ULong;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_org_danbrough_klog_KLogger;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionConfig;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Any;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod_PASSWORD;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod_KEY;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod_KEYBOARD;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SshUtils;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_Scope;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_Channel;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionNative;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SSHChannel;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SSHSession;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SSHScope;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlinx_cinterop_MemScope;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SshUtilsImpl;

extern void Java_io_github_danbrough_kssh2_LibSSH2_testJNI(void* env);
extern void Java_io_github_danbrough_kssh2_TestSomethingKt_testSomething(void* env);
extern void Java_io_github_danbrough_kssh2_AndroidKt_testLog(void* env);

typedef struct {
  /* Service functions. */
  void (*DisposeStablePointer)(libkssh2_KNativePtr ptr);
  void (*DisposeString)(const char* string);
  libkssh2_KBoolean (*IsInstance)(libkssh2_KNativePtr ref, const libkssh2_KType* type);
  libkssh2_kref_kotlin_Byte (*createNullableByte)(libkssh2_KByte);
  libkssh2_KByte (*getNonNullValueOfByte)(libkssh2_kref_kotlin_Byte);
  libkssh2_kref_kotlin_Short (*createNullableShort)(libkssh2_KShort);
  libkssh2_KShort (*getNonNullValueOfShort)(libkssh2_kref_kotlin_Short);
  libkssh2_kref_kotlin_Int (*createNullableInt)(libkssh2_KInt);
  libkssh2_KInt (*getNonNullValueOfInt)(libkssh2_kref_kotlin_Int);
  libkssh2_kref_kotlin_Long (*createNullableLong)(libkssh2_KLong);
  libkssh2_KLong (*getNonNullValueOfLong)(libkssh2_kref_kotlin_Long);
  libkssh2_kref_kotlin_Float (*createNullableFloat)(libkssh2_KFloat);
  libkssh2_KFloat (*getNonNullValueOfFloat)(libkssh2_kref_kotlin_Float);
  libkssh2_kref_kotlin_Double (*createNullableDouble)(libkssh2_KDouble);
  libkssh2_KDouble (*getNonNullValueOfDouble)(libkssh2_kref_kotlin_Double);
  libkssh2_kref_kotlin_Char (*createNullableChar)(libkssh2_KChar);
  libkssh2_KChar (*getNonNullValueOfChar)(libkssh2_kref_kotlin_Char);
  libkssh2_kref_kotlin_Boolean (*createNullableBoolean)(libkssh2_KBoolean);
  libkssh2_KBoolean (*getNonNullValueOfBoolean)(libkssh2_kref_kotlin_Boolean);
  libkssh2_kref_kotlin_Unit (*createNullableUnit)(void);
  libkssh2_kref_kotlin_UByte (*createNullableUByte)(libkssh2_KUByte);
  libkssh2_KUByte (*getNonNullValueOfUByte)(libkssh2_kref_kotlin_UByte);
  libkssh2_kref_kotlin_UShort (*createNullableUShort)(libkssh2_KUShort);
  libkssh2_KUShort (*getNonNullValueOfUShort)(libkssh2_kref_kotlin_UShort);
  libkssh2_kref_kotlin_UInt (*createNullableUInt)(libkssh2_KUInt);
  libkssh2_KUInt (*getNonNullValueOfUInt)(libkssh2_kref_kotlin_UInt);
  libkssh2_kref_kotlin_ULong (*createNullableULong)(libkssh2_KULong);
  libkssh2_KULong (*getNonNullValueOfULong)(libkssh2_kref_kotlin_ULong);

  /* User functions. */
  struct {
    struct {
      struct {
        struct {
          struct {
            struct {
              struct {
                struct {
                  struct {
                    libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod (*get)(); /* enum entry for PASSWORD. */
                  } PASSWORD;
                  struct {
                    libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod (*get)(); /* enum entry for KEY. */
                  } KEY;
                  struct {
                    libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod (*get)(); /* enum entry for KEYBOARD. */
                  } KEYBOARD;
                  libkssh2_KType* (*_type)(void);
                } AuthMethod;
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SessionConfig (*SessionConfig)(const char* user, const char* hostName, libkssh2_KInt port, const char* password, const char* publicKeyFile, const char* privateKeyFile, const char* knownHostsFile, libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod authMethod);
                libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod (*get_authMethod)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*get_hostName)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*get_knownHostsFile)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*get_password)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                libkssh2_KInt (*get_port)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*get_privateKeyFile)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*get_publicKeyFile)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*get_user)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*component1)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*component2)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                libkssh2_KInt (*component3)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*component4)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*component5)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*component6)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*component7)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod (*component8)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                libkssh2_kref_io_github_danbrough_kssh2_SessionConfig (*copy)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz, const char* user, const char* hostName, libkssh2_KInt port, const char* password, const char* publicKeyFile, const char* privateKeyFile, const char* knownHostsFile, libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod authMethod);
                libkssh2_KBoolean (*equals)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz, libkssh2_kref_kotlin_Any other);
                libkssh2_KInt (*hashCode)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
                const char* (*toString)(libkssh2_kref_io_github_danbrough_kssh2_SessionConfig thiz);
              } SessionConfig;
              struct {
                void (*testJNI)(void* env);
                void (*testSomething)(void* env);
                void (*testLog)(void* env);
              } jni;
              struct {
                libkssh2_KType* (*_type)(void);
                const char* (*getEnv)(libkssh2_kref_io_github_danbrough_kssh2_SshUtils thiz, const char* name);
                const char* (*threadName)(libkssh2_kref_io_github_danbrough_kssh2_SshUtils thiz);
              } SshUtils;
              struct {
                libkssh2_KType* (*_type)(void);
              } Scope;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_Channel (*Channel)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative session, void* channel);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_Channel thiz);
                void (*exec)(libkssh2_kref_io_github_danbrough_kssh2_Channel thiz, const char* commandline);
                void (*readLoop)(libkssh2_kref_io_github_danbrough_kssh2_Channel thiz);
              } Channel;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SSHChannel (*SSHChannel)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession session, void* channel);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz);
                void (*exec)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz, const char* commandLine);
                void (*shell)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz);
              } SSHChannel;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SSHScope (*SSHScope)(libkssh2_kref_kotlinx_cinterop_MemScope mScope);
                libkssh2_kref_kotlinx_cinterop_MemScope (*get_mScope)(libkssh2_kref_io_github_danbrough_kssh2_SSHScope thiz);
                void* (*alloc)(libkssh2_kref_io_github_danbrough_kssh2_SSHScope thiz, libkssh2_KInt size, libkssh2_KInt align);
                void* (*alloc_)(libkssh2_kref_io_github_danbrough_kssh2_SSHScope thiz, libkssh2_KLong size, libkssh2_KInt align);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_SSHScope thiz);
              } SSHScope;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SSHSession (*SSHSession)(libkssh2_KBoolean useAgent, libkssh2_kref_io_github_danbrough_kssh2_SSHScope ssh);
                void* (*get_agent)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz);
                void (*set_agent)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz, void* set);
                void* (*get_session)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz);
                void (*set_session)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz, void* set);
                libkssh2_KInt (*get_sock)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz);
                void (*set_sock)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz, libkssh2_KInt set);
                libkssh2_kref_io_github_danbrough_kssh2_SSHScope (*get_ssh)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz);
                libkssh2_KBoolean (*get_useAgent)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz);
                void (*authenticate)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz, const char* user, const char* publicKeyPath, const char* privateKeyPath, const char* password);
                libkssh2_kref_io_github_danbrough_kssh2_SSHChannel (*channel)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz);
                void (*connect)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz, const char* hostName, libkssh2_KInt port);
                libkssh2_KInt (*waitSocket)(libkssh2_kref_io_github_danbrough_kssh2_SSHSession thiz);
              } SSHSession;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SessionConfig (*get_config)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative thiz);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative thiz);
                libkssh2_kref_io_github_danbrough_kssh2_Channel (*openChannel)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative thiz);
                void (*waitSocket)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative thiz);
              } SessionNative;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SshUtilsImpl (*_instance)();
                const char* (*getEnv)(libkssh2_kref_io_github_danbrough_kssh2_SshUtilsImpl thiz, const char* name);
                const char* (*threadName)(libkssh2_kref_io_github_danbrough_kssh2_SshUtilsImpl thiz);
              } SshUtilsImpl;
              libkssh2_kref_org_danbrough_klog_KLogger (*get_log)();
            } kssh2;
          } danbrough;
        } github;
      } io;
    } root;
  } kotlin;
} libkssh2_ExportedSymbols;
extern libkssh2_ExportedSymbols* libkssh2_symbols(void);
#ifdef __cplusplus
}  /* extern "C" */
#endif
#endif  /* KONAN_LIBKSSH2_H */
