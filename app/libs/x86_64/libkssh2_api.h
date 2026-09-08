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
} libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SSHChannel;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_Result;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_Any;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_Result_Companion;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_org_danbrough_klog_KLogger;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_Channel;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_Session;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlinx_coroutines_flow_Flow;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SSHScope;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_coroutines_CoroutineContext_Key;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SSHScope_ContextKey;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionConfig;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionConfig_AuthMethod;
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
} libkssh2_kref_io_github_danbrough_kssh2_IPAddressValidator;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_Scope;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_ChannelOld;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SessionNative;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_LibSSH2;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Socket;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Agent;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_ByteArray;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SshChannelNative;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_io_github_danbrough_kssh2_SshUtils;
typedef struct {
  libkssh2_KNativePtr pinned;
} libkssh2_kref_kotlin_collections_List;

extern void Java_io_github_danbrough_kssh2_LibSSH2_00024Channel_close(void* env, void* clazz, libkssh2_KLong channel);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Channel_channelOpen(void* env, void* clazz, libkssh2_KLong session, libkssh2_KLong socket, void* channelType, libkssh2_KInt windowSize, libkssh2_KInt packetSize, void* message);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Channel_processStartup(void* env, void* clazz, libkssh2_KLong sessionPtr, libkssh2_KLong socketHandle, libkssh2_KLong channelPtr, void* request, void* message);
extern libkssh2_KInt Java_io_github_danbrough_kssh2_LibSSH2_00024Channel_read(void* env, void* clazz, libkssh2_KLong sessionPtr, libkssh2_KLong socketHandle, libkssh2_KLong channelPtr, libkssh2_KInt streamId, void* buffer);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Channel_requestPty(void* env, void* clazz, libkssh2_KLong channel, void* terminalType);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Channel_write(void* env, void* clazz, libkssh2_KLong channel, void* data);
extern libkssh2_KInt Java_io_github_danbrough_kssh2_SshChannelJVM_sshChannelRead(void* env, void* thiz, libkssh2_KLong channelPtr, libkssh2_KInt streamId, void* buffer, libkssh2_KInt position, libkssh2_KInt remaining);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Session_createSession(void* env, void* clazz, libkssh2_KUByte blocking);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Session_authenticateWithAgent(void* env, void* clazz, libkssh2_KLong session, libkssh2_KLong socket, void* remoteUser);
extern void Java_io_github_danbrough_kssh2_LibSSH2_00024Session_close(void* env, void* clazz, libkssh2_KLong ptr);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Session_sessionHandshake(void* env, void* clazz, libkssh2_KLong ptrSession, libkssh2_KLong ptrSocket);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Session_waitSocket(void* env, void* clazz, libkssh2_KLong ptrSession, libkssh2_KLong ptrSocket);
extern void Java_io_github_danbrough_kssh2_LibSSH2_00024Socket_close(void* env, void* clazz, libkssh2_KLong socket);
extern libkssh2_KLong Java_io_github_danbrough_kssh2_LibSSH2_00024Socket_connect(void* env, void* clazz, void* hostName, libkssh2_KInt port);
extern void Java_io_github_danbrough_kssh2_LibSSH2_00024Agent_close(void* env, void* clazz, libkssh2_KLong agent);
extern void Java_io_github_danbrough_kssh2_LibSSH2_closeLib(void* env);
extern void Java_io_github_danbrough_kssh2_LibSSH2_initLib(void* env);
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
                  libkssh2_KType* (*_type)(void);
                  libkssh2_kref_io_github_danbrough_kssh2_Result_Companion (*_instance)();
                  libkssh2_kref_io_github_danbrough_kssh2_Result (*get_Success)(libkssh2_kref_io_github_danbrough_kssh2_Result_Companion thiz);
                } Companion;
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_Result (*Result)(libkssh2_KInt code, const char* message);
                libkssh2_KInt (*get_code)(libkssh2_kref_io_github_danbrough_kssh2_Result thiz);
                const char* (*get_message)(libkssh2_kref_io_github_danbrough_kssh2_Result thiz);
                libkssh2_KInt (*component1)(libkssh2_kref_io_github_danbrough_kssh2_Result thiz);
                const char* (*component2)(libkssh2_kref_io_github_danbrough_kssh2_Result thiz);
                libkssh2_kref_io_github_danbrough_kssh2_Result (*copy)(libkssh2_kref_io_github_danbrough_kssh2_Result thiz, libkssh2_KInt code, const char* message);
                libkssh2_KBoolean (*equals)(libkssh2_kref_io_github_danbrough_kssh2_Result thiz, libkssh2_kref_kotlin_Any other);
                libkssh2_KInt (*hashCode)(libkssh2_kref_io_github_danbrough_kssh2_Result thiz);
                const char* (*toString)(libkssh2_kref_io_github_danbrough_kssh2_Result thiz);
              } Result;
              struct {
                void (*ssh2ChannelClose)(void* env, void* clazz, libkssh2_KLong channel);
                libkssh2_KLong (*ssh2ChannelOpen)(void* env, void* clazz, libkssh2_KLong session, libkssh2_KLong socket, void* channelType, libkssh2_KInt windowSize, libkssh2_KInt packetSize, void* message);
                libkssh2_KLong (*ssh2ChannelProcessStartup)(void* env, void* clazz, libkssh2_KLong sessionPtr, libkssh2_KLong socketHandle, libkssh2_KLong channelPtr, void* request, void* message);
                libkssh2_KInt (*ssh2ChannelRead)(void* env, void* clazz, libkssh2_KLong sessionPtr, libkssh2_KLong socketHandle, libkssh2_KLong channelPtr, libkssh2_KInt streamId, void* buffer);
                libkssh2_KLong (*ssh2ChannelRequestPty)(void* env, void* clazz, libkssh2_KLong channel, void* terminalType);
                libkssh2_KLong (*ssh2ChannelWrite)(void* env, void* clazz, libkssh2_KLong channel, void* data);
                libkssh2_KInt (*sshChannelRead)(void* env, void* thiz, libkssh2_KLong channelPtr, libkssh2_KInt streamId, void* buffer, libkssh2_KInt position, libkssh2_KInt remaining);
                libkssh2_KLong (*ssh2CreateSession)(void* env, void* clazz, libkssh2_KUByte blocking);
                libkssh2_KLong (*ssh2SessionAuthenticateWithAgent)(void* env, void* clazz, libkssh2_KLong session, libkssh2_KLong socket, void* remoteUser);
                void (*ssh2SessionClose)(void* env, void* clazz, libkssh2_KLong ptr);
                libkssh2_KLong (*ssh2SessionHandshake)(void* env, void* clazz, libkssh2_KLong ptrSession, libkssh2_KLong ptrSocket);
                libkssh2_KLong (*ssh2SessionWaitsocket)(void* env, void* clazz, libkssh2_KLong ptrSession, libkssh2_KLong ptrSocket);
                void (*ssh2SocketClose)(void* env, void* clazz, libkssh2_KLong socket);
                libkssh2_KLong (*ssh2SocketConnect)(void* env, void* clazz, void* hostName, libkssh2_KInt port);
                const char* (*get_JNI_PREFIX)();
                libkssh2_kref_org_danbrough_klog_KLogger (*get_log)();
                void (*ssh2AgentClose)(void* env, void* clazz, libkssh2_KLong agent);
                void (*ssh2Close)(void* env);
                void (*ssh2Init)(void* env);
                void (*testLog)(void* env);
              } jni;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_Channel (*Channel)(libkssh2_kref_io_github_danbrough_kssh2_Session session, const char* channelType);
                libkssh2_KLong (*get_channelPtr)(libkssh2_kref_io_github_danbrough_kssh2_Channel thiz);
                void (*set_channelPtr)(libkssh2_kref_io_github_danbrough_kssh2_Channel thiz, libkssh2_KLong set);
                libkssh2_kref_io_github_danbrough_kssh2_Session (*get_session)(libkssh2_kref_io_github_danbrough_kssh2_Channel thiz);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_Channel thiz);
                libkssh2_kref_kotlinx_coroutines_flow_Flow (*readChannel)(libkssh2_kref_io_github_danbrough_kssh2_Channel thiz, libkssh2_KInt bufSize);
              } Channel;
              struct {
                struct {
                  libkssh2_KType* (*_type)(void);
                  libkssh2_kref_io_github_danbrough_kssh2_SSHScope_ContextKey (*_instance)();
                } ContextKey;
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SSHScope (*SSHScope)();
                libkssh2_kref_kotlin_coroutines_CoroutineContext_Key (*get_key)(libkssh2_kref_io_github_danbrough_kssh2_SSHScope thiz);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_SSHScope thiz);
              } SSHScope;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_Session (*Session)();
                libkssh2_KLong (*get_agent)(libkssh2_kref_io_github_danbrough_kssh2_Session thiz);
                void (*set_agent)(libkssh2_kref_io_github_danbrough_kssh2_Session thiz, libkssh2_KLong set);
                libkssh2_KLong (*get_session)(libkssh2_kref_io_github_danbrough_kssh2_Session thiz);
                libkssh2_KLong (*get_socket)(libkssh2_kref_io_github_danbrough_kssh2_Session thiz);
                void (*set_socket)(libkssh2_kref_io_github_danbrough_kssh2_Session thiz, libkssh2_KLong set);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_Session thiz);
              } Session;
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
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_IPAddressValidator (*_instance)();
                libkssh2_KBoolean (*isIPAddress)(libkssh2_kref_io_github_danbrough_kssh2_IPAddressValidator thiz, const char* input);
                libkssh2_KBoolean (*isIPv4)(libkssh2_kref_io_github_danbrough_kssh2_IPAddressValidator thiz, const char* input);
                libkssh2_KBoolean (*isIPv6)(libkssh2_kref_io_github_danbrough_kssh2_IPAddressValidator thiz, const char* input);
              } IPAddressValidator;
              struct {
                libkssh2_KType* (*_type)(void);
              } Scope;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_ChannelOld (*ChannelOld)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative session, void* channel);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_ChannelOld thiz);
                void (*exec)(libkssh2_kref_io_github_danbrough_kssh2_ChannelOld thiz, const char* commandline);
                void (*readLoop)(libkssh2_kref_io_github_danbrough_kssh2_ChannelOld thiz);
              } ChannelOld;
              struct {
                struct {
                  libkssh2_KType* (*_type)(void);
                  libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Socket (*_instance)();
                  void (*close)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Socket thiz, libkssh2_KLong socket);
                  libkssh2_KLong (*connect)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Socket thiz, const char* hostName, libkssh2_KInt port);
                } Socket;
                struct {
                  libkssh2_KType* (*_type)(void);
                  libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session (*_instance)();
                  libkssh2_kref_io_github_danbrough_kssh2_Result (*authenticatePassword)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session thiz, libkssh2_KLong sessionPtr, libkssh2_KLong socket, const char* userName, const char* password);
                  libkssh2_KInt (*authenticatePublicKey)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session thiz, libkssh2_KLong sessionPtr, libkssh2_KLong socket, const char* user, const char* publicKeyData, const char* privateKeyData, const char* passphrase);
                  libkssh2_KLong (*authenticateWithAgent)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session thiz, libkssh2_KLong session, libkssh2_KLong socket, const char* remoteUser);
                  void (*close)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session thiz, libkssh2_KLong session);
                  libkssh2_KLong (*createSession)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session thiz, libkssh2_KBoolean blocking);
                  libkssh2_KLong (*sessionHandshake)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session thiz, libkssh2_KLong session, libkssh2_KLong socket);
                  libkssh2_KLong (*waitSocket)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Session thiz, libkssh2_KLong session, libkssh2_KLong socket);
                } Session;
                struct {
                  libkssh2_KType* (*_type)(void);
                  libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Agent (*_instance)();
                  void (*close)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Agent thiz, libkssh2_KLong agent);
                } Agent;
                struct {
                  libkssh2_KType* (*_type)(void);
                  libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel (*_instance)();
                  libkssh2_KLong (*channelOpen)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel thiz, libkssh2_KLong session, libkssh2_KLong socket, const char* channelType, libkssh2_KInt windowSize, libkssh2_KInt packetSize, const char* message);
                  void (*close)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel thiz, libkssh2_KLong channel);
                  libkssh2_KLong (*exec)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel thiz, libkssh2_KLong channel, const char* cmdLine);
                  libkssh2_KLong (*processStartup)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel thiz, libkssh2_KLong sessionPtr, libkssh2_KLong socketHandle, libkssh2_KLong channel, const char* request, const char* message);
                  libkssh2_KInt (*read)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel thiz, libkssh2_KLong session, libkssh2_KLong socketHandle, libkssh2_KLong channelPtr, libkssh2_KInt streamId, libkssh2_kref_kotlin_ByteArray buffer);
                  libkssh2_KLong (*requestPty)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel thiz, libkssh2_KLong channelPtr, const char* terminal);
                  libkssh2_KLong (*write)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2_Channel thiz, libkssh2_KLong channel, const char* data);
                } Channel;
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_LibSSH2 (*_instance)();
                void (*closeLib)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2 thiz);
                void (*initLib)(libkssh2_kref_io_github_danbrough_kssh2_LibSSH2 thiz);
              } LibSSH2;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SSHChannel (*SSHChannel)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld session, void* channel);
                libkssh2_KBoolean (*get_cancel)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz);
                void (*set_cancel)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz, libkssh2_KBoolean set);
                void* (*get_channel)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz);
                void (*exec)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz, const char* commandLine);
                void (*setEnvironment)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz, const char* name, const char* value);
                void (*shell)(libkssh2_kref_io_github_danbrough_kssh2_SSHChannel thiz);
              } SSHChannel;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld (*SSHSessionOld)(libkssh2_KBoolean useAgent, libkssh2_kref_io_github_danbrough_kssh2_SSHScope ssh);
                void* (*get_agent)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz);
                void (*set_agent)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz, void* set);
                void* (*get_session)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz);
                void (*set_session)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz, void* set);
                libkssh2_KInt (*get_sock)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz);
                void (*set_sock)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz, libkssh2_KInt set);
                libkssh2_kref_io_github_danbrough_kssh2_SSHScope (*get_ssh)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz);
                libkssh2_KBoolean (*get_useAgent)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz);
                void (*authenticatePubKey)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz, const char* user, const char* publicKeyPath, const char* privateKeyPath, const char* password);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz);
                void (*connect)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz, const char* hostName, libkssh2_KInt port);
                libkssh2_KInt (*waitSocket)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz);
              } SSHSessionOld;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SessionConfig (*get_config)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative thiz);
                void (*close)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative thiz);
                libkssh2_kref_io_github_danbrough_kssh2_ChannelOld (*openChannel)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative thiz);
                void (*waitSocket)(libkssh2_kref_io_github_danbrough_kssh2_SessionNative thiz);
              } SessionNative;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SshChannelNative (*SshChannelNative)(void* channelPointer);
              } SshChannelNative;
              struct {
                libkssh2_KType* (*_type)(void);
                libkssh2_kref_io_github_danbrough_kssh2_SshUtils (*_instance)();
                const char* (*getEnv)(libkssh2_kref_io_github_danbrough_kssh2_SshUtils thiz, const char* name);
                libkssh2_kref_kotlin_collections_List (*resolveHostName)(libkssh2_kref_io_github_danbrough_kssh2_SshUtils thiz, const char* hostName);
                const char* (*threadName)(libkssh2_kref_io_github_danbrough_kssh2_SshUtils thiz);
              } SshUtils;
              libkssh2_kref_io_github_danbrough_kssh2_SSHChannel (*channel)(libkssh2_kref_io_github_danbrough_kssh2_SSHSessionOld thiz);
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
