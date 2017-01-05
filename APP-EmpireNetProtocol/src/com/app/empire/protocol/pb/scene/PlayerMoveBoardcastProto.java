// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scene/PlayerMoveBoardcastMsg.proto

package com.app.empire.protocol.pb.scene;

public final class PlayerMoveBoardcastProto {
  private PlayerMoveBoardcastProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface PlayerMoveBoardcastMsgOrBuilder extends
      // @@protoc_insertion_point(interface_extends:PlayerMoveBoardcastMsg)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional int64 id = 1;</code>
     *
     * <pre>
     *玩家id
     * </pre>
     */
    boolean hasId();
    /**
     * <code>optional int64 id = 1;</code>
     *
     * <pre>
     *玩家id
     * </pre>
     */
    long getId();

    /**
     * <code>optional .PBVector3 cur = 2;</code>
     *
     * <pre>
     *当前位置
     * </pre>
     */
    boolean hasCur();
    /**
     * <code>optional .PBVector3 cur = 2;</code>
     *
     * <pre>
     *当前位置
     * </pre>
     */
    com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getCur();
    /**
     * <code>optional .PBVector3 cur = 2;</code>
     *
     * <pre>
     *当前位置
     * </pre>
     */
    com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getCurOrBuilder();

    /**
     * <code>optional .PBVector3 tar = 3;</code>
     *
     * <pre>
     *目标位置
     * </pre>
     */
    boolean hasTar();
    /**
     * <code>optional .PBVector3 tar = 3;</code>
     *
     * <pre>
     *目标位置
     * </pre>
     */
    com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getTar();
    /**
     * <code>optional .PBVector3 tar = 3;</code>
     *
     * <pre>
     *目标位置
     * </pre>
     */
    com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getTarOrBuilder();

    /**
     * <code>optional int64 preArriveTargetServerTime = 4;</code>
     *
     * <pre>
     *服务端当前时间戳
     * </pre>
     */
    boolean hasPreArriveTargetServerTime();
    /**
     * <code>optional int64 preArriveTargetServerTime = 4;</code>
     *
     * <pre>
     *服务端当前时间戳
     * </pre>
     */
    long getPreArriveTargetServerTime();
  }
  /**
   * Protobuf type {@code PlayerMoveBoardcastMsg}
   */
  public static final class PlayerMoveBoardcastMsg extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:PlayerMoveBoardcastMsg)
      PlayerMoveBoardcastMsgOrBuilder {
    // Use PlayerMoveBoardcastMsg.newBuilder() to construct.
    private PlayerMoveBoardcastMsg(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private PlayerMoveBoardcastMsg(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final PlayerMoveBoardcastMsg defaultInstance;
    public static PlayerMoveBoardcastMsg getDefaultInstance() {
      return defaultInstance;
    }

    public PlayerMoveBoardcastMsg getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private PlayerMoveBoardcastMsg(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              id_ = input.readInt64();
              break;
            }
            case 18: {
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder subBuilder = null;
              if (((bitField0_ & 0x00000002) == 0x00000002)) {
                subBuilder = cur_.toBuilder();
              }
              cur_ = input.readMessage(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(cur_);
                cur_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000002;
              break;
            }
            case 26: {
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder subBuilder = null;
              if (((bitField0_ & 0x00000004) == 0x00000004)) {
                subBuilder = tar_.toBuilder();
              }
              tar_ = input.readMessage(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(tar_);
                tar_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000004;
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              preArriveTargetServerTime_ = input.readInt64();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.internal_static_PlayerMoveBoardcastMsg_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.internal_static_PlayerMoveBoardcastMsg_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg.class, com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg.Builder.class);
    }

    public static com.google.protobuf.Parser<PlayerMoveBoardcastMsg> PARSER =
        new com.google.protobuf.AbstractParser<PlayerMoveBoardcastMsg>() {
      public PlayerMoveBoardcastMsg parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PlayerMoveBoardcastMsg(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<PlayerMoveBoardcastMsg> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int ID_FIELD_NUMBER = 1;
    private long id_;
    /**
     * <code>optional int64 id = 1;</code>
     *
     * <pre>
     *玩家id
     * </pre>
     */
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional int64 id = 1;</code>
     *
     * <pre>
     *玩家id
     * </pre>
     */
    public long getId() {
      return id_;
    }

    public static final int CUR_FIELD_NUMBER = 2;
    private com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 cur_;
    /**
     * <code>optional .PBVector3 cur = 2;</code>
     *
     * <pre>
     *当前位置
     * </pre>
     */
    public boolean hasCur() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional .PBVector3 cur = 2;</code>
     *
     * <pre>
     *当前位置
     * </pre>
     */
    public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getCur() {
      return cur_;
    }
    /**
     * <code>optional .PBVector3 cur = 2;</code>
     *
     * <pre>
     *当前位置
     * </pre>
     */
    public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getCurOrBuilder() {
      return cur_;
    }

    public static final int TAR_FIELD_NUMBER = 3;
    private com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 tar_;
    /**
     * <code>optional .PBVector3 tar = 3;</code>
     *
     * <pre>
     *目标位置
     * </pre>
     */
    public boolean hasTar() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional .PBVector3 tar = 3;</code>
     *
     * <pre>
     *目标位置
     * </pre>
     */
    public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getTar() {
      return tar_;
    }
    /**
     * <code>optional .PBVector3 tar = 3;</code>
     *
     * <pre>
     *目标位置
     * </pre>
     */
    public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getTarOrBuilder() {
      return tar_;
    }

    public static final int PREARRIVETARGETSERVERTIME_FIELD_NUMBER = 4;
    private long preArriveTargetServerTime_;
    /**
     * <code>optional int64 preArriveTargetServerTime = 4;</code>
     *
     * <pre>
     *服务端当前时间戳
     * </pre>
     */
    public boolean hasPreArriveTargetServerTime() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>optional int64 preArriveTargetServerTime = 4;</code>
     *
     * <pre>
     *服务端当前时间戳
     * </pre>
     */
    public long getPreArriveTargetServerTime() {
      return preArriveTargetServerTime_;
    }

    private void initFields() {
      id_ = 0L;
      cur_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
      tar_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
      preArriveTargetServerTime_ = 0L;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt64(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeMessage(2, cur_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeMessage(3, tar_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt64(4, preArriveTargetServerTime_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, cur_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, tar_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(4, preArriveTargetServerTime_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code PlayerMoveBoardcastMsg}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:PlayerMoveBoardcastMsg)
        com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsgOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.internal_static_PlayerMoveBoardcastMsg_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.internal_static_PlayerMoveBoardcastMsg_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg.class, com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg.Builder.class);
      }

      // Construct using com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getCurFieldBuilder();
          getTarFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        id_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000001);
        if (curBuilder_ == null) {
          cur_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
        } else {
          curBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        if (tarBuilder_ == null) {
          tar_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
        } else {
          tarBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000004);
        preArriveTargetServerTime_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.internal_static_PlayerMoveBoardcastMsg_descriptor;
      }

      public com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg getDefaultInstanceForType() {
        return com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg.getDefaultInstance();
      }

      public com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg build() {
        com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg buildPartial() {
        com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg result = new com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        if (curBuilder_ == null) {
          result.cur_ = cur_;
        } else {
          result.cur_ = curBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        if (tarBuilder_ == null) {
          result.tar_ = tar_;
        } else {
          result.tar_ = tarBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.preArriveTargetServerTime_ = preArriveTargetServerTime_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg) {
          return mergeFrom((com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg other) {
        if (other == com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg.getDefaultInstance()) return this;
        if (other.hasId()) {
          setId(other.getId());
        }
        if (other.hasCur()) {
          mergeCur(other.getCur());
        }
        if (other.hasTar()) {
          mergeTar(other.getTar());
        }
        if (other.hasPreArriveTargetServerTime()) {
          setPreArriveTargetServerTime(other.getPreArriveTargetServerTime());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private long id_ ;
      /**
       * <code>optional int64 id = 1;</code>
       *
       * <pre>
       *玩家id
       * </pre>
       */
      public boolean hasId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional int64 id = 1;</code>
       *
       * <pre>
       *玩家id
       * </pre>
       */
      public long getId() {
        return id_;
      }
      /**
       * <code>optional int64 id = 1;</code>
       *
       * <pre>
       *玩家id
       * </pre>
       */
      public Builder setId(long value) {
        bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 id = 1;</code>
       *
       * <pre>
       *玩家id
       * </pre>
       */
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = 0L;
        onChanged();
        return this;
      }

      private com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 cur_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder> curBuilder_;
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      public boolean hasCur() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getCur() {
        if (curBuilder_ == null) {
          return cur_;
        } else {
          return curBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      public Builder setCur(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 value) {
        if (curBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          cur_ = value;
          onChanged();
        } else {
          curBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      public Builder setCur(
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder builderForValue) {
        if (curBuilder_ == null) {
          cur_ = builderForValue.build();
          onChanged();
        } else {
          curBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      public Builder mergeCur(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 value) {
        if (curBuilder_ == null) {
          if (((bitField0_ & 0x00000002) == 0x00000002) &&
              cur_ != com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance()) {
            cur_ =
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.newBuilder(cur_).mergeFrom(value).buildPartial();
          } else {
            cur_ = value;
          }
          onChanged();
        } else {
          curBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      public Builder clearCur() {
        if (curBuilder_ == null) {
          cur_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
          onChanged();
        } else {
          curBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder getCurBuilder() {
        bitField0_ |= 0x00000002;
        onChanged();
        return getCurFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getCurOrBuilder() {
        if (curBuilder_ != null) {
          return curBuilder_.getMessageOrBuilder();
        } else {
          return cur_;
        }
      }
      /**
       * <code>optional .PBVector3 cur = 2;</code>
       *
       * <pre>
       *当前位置
       * </pre>
       */
      private com.google.protobuf.SingleFieldBuilder<
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder> 
          getCurFieldBuilder() {
        if (curBuilder_ == null) {
          curBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder>(
                  getCur(),
                  getParentForChildren(),
                  isClean());
          cur_ = null;
        }
        return curBuilder_;
      }

      private com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 tar_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder> tarBuilder_;
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      public boolean hasTar() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getTar() {
        if (tarBuilder_ == null) {
          return tar_;
        } else {
          return tarBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      public Builder setTar(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 value) {
        if (tarBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          tar_ = value;
          onChanged();
        } else {
          tarBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      public Builder setTar(
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder builderForValue) {
        if (tarBuilder_ == null) {
          tar_ = builderForValue.build();
          onChanged();
        } else {
          tarBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      public Builder mergeTar(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 value) {
        if (tarBuilder_ == null) {
          if (((bitField0_ & 0x00000004) == 0x00000004) &&
              tar_ != com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance()) {
            tar_ =
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.newBuilder(tar_).mergeFrom(value).buildPartial();
          } else {
            tar_ = value;
          }
          onChanged();
        } else {
          tarBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      public Builder clearTar() {
        if (tarBuilder_ == null) {
          tar_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
          onChanged();
        } else {
          tarBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder getTarBuilder() {
        bitField0_ |= 0x00000004;
        onChanged();
        return getTarFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getTarOrBuilder() {
        if (tarBuilder_ != null) {
          return tarBuilder_.getMessageOrBuilder();
        } else {
          return tar_;
        }
      }
      /**
       * <code>optional .PBVector3 tar = 3;</code>
       *
       * <pre>
       *目标位置
       * </pre>
       */
      private com.google.protobuf.SingleFieldBuilder<
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder> 
          getTarFieldBuilder() {
        if (tarBuilder_ == null) {
          tarBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder>(
                  getTar(),
                  getParentForChildren(),
                  isClean());
          tar_ = null;
        }
        return tarBuilder_;
      }

      private long preArriveTargetServerTime_ ;
      /**
       * <code>optional int64 preArriveTargetServerTime = 4;</code>
       *
       * <pre>
       *服务端当前时间戳
       * </pre>
       */
      public boolean hasPreArriveTargetServerTime() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>optional int64 preArriveTargetServerTime = 4;</code>
       *
       * <pre>
       *服务端当前时间戳
       * </pre>
       */
      public long getPreArriveTargetServerTime() {
        return preArriveTargetServerTime_;
      }
      /**
       * <code>optional int64 preArriveTargetServerTime = 4;</code>
       *
       * <pre>
       *服务端当前时间戳
       * </pre>
       */
      public Builder setPreArriveTargetServerTime(long value) {
        bitField0_ |= 0x00000008;
        preArriveTargetServerTime_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 preArriveTargetServerTime = 4;</code>
       *
       * <pre>
       *服务端当前时间戳
       * </pre>
       */
      public Builder clearPreArriveTargetServerTime() {
        bitField0_ = (bitField0_ & ~0x00000008);
        preArriveTargetServerTime_ = 0L;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:PlayerMoveBoardcastMsg)
    }

    static {
      defaultInstance = new PlayerMoveBoardcastMsg(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:PlayerMoveBoardcastMsg)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_PlayerMoveBoardcastMsg_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_PlayerMoveBoardcastMsg_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\"scene/PlayerMoveBoardcastMsg.proto\032\030wa" +
      "rField/PBVector3.proto\"y\n\026PlayerMoveBoar" +
      "dcastMsg\022\n\n\002id\030\001 \001(\003\022\027\n\003cur\030\002 \001(\0132\n.PBVe" +
      "ctor3\022\027\n\003tar\030\003 \001(\0132\n.PBVector3\022!\n\031preArr" +
      "iveTargetServerTime\030\004 \001(\003B<\n com.app.emp" +
      "ire.protocol.pb.sceneB\030PlayerMoveBoardca" +
      "stProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.app.empire.protocol.pb.warField.Vector3Proto.getDescriptor(),
        }, assigner);
    internal_static_PlayerMoveBoardcastMsg_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_PlayerMoveBoardcastMsg_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_PlayerMoveBoardcastMsg_descriptor,
        new java.lang.String[] { "Id", "Cur", "Tar", "PreArriveTargetServerTime", });
    com.app.empire.protocol.pb.warField.Vector3Proto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}