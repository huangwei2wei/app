// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: warField/PostionMsg.proto

package com.app.empire.protocol.pb.warField;

public final class PostionMsgProto {
  private PostionMsgProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface PostionMsgOrBuilder extends
      // @@protoc_insertion_point(interface_extends:PostionMsg)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional int32 map_id = 1;</code>
     *
     * <pre>
     *地图真实ID
     * </pre>
     */
    boolean hasMapId();
    /**
     * <code>optional int32 map_id = 1;</code>
     *
     * <pre>
     *地图真实ID
     * </pre>
     */
    int getMapId();

    /**
     * <code>optional int32 map_key = 2;</code>
     *
     * <pre>
     *地图模型ID
     * </pre>
     */
    boolean hasMapKey();
    /**
     * <code>optional int32 map_key = 2;</code>
     *
     * <pre>
     *地图模型ID
     * </pre>
     */
    int getMapKey();

    /**
     * <code>optional .PBVector3 postion = 3;</code>
     *
     * <pre>
     *坐标
     * </pre>
     */
    boolean hasPostion();
    /**
     * <code>optional .PBVector3 postion = 3;</code>
     *
     * <pre>
     *坐标
     * </pre>
     */
    com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getPostion();
    /**
     * <code>optional .PBVector3 postion = 3;</code>
     *
     * <pre>
     *坐标
     * </pre>
     */
    com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getPostionOrBuilder();
  }
  /**
   * Protobuf type {@code PostionMsg}
   */
  public static final class PostionMsg extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:PostionMsg)
      PostionMsgOrBuilder {
    // Use PostionMsg.newBuilder() to construct.
    private PostionMsg(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private PostionMsg(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final PostionMsg defaultInstance;
    public static PostionMsg getDefaultInstance() {
      return defaultInstance;
    }

    public PostionMsg getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private PostionMsg(
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
              mapId_ = input.readInt32();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              mapKey_ = input.readInt32();
              break;
            }
            case 26: {
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder subBuilder = null;
              if (((bitField0_ & 0x00000004) == 0x00000004)) {
                subBuilder = postion_.toBuilder();
              }
              postion_ = input.readMessage(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(postion_);
                postion_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000004;
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
      return com.app.empire.protocol.pb.warField.PostionMsgProto.internal_static_PostionMsg_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.app.empire.protocol.pb.warField.PostionMsgProto.internal_static_PostionMsg_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg.class, com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg.Builder.class);
    }

    public static com.google.protobuf.Parser<PostionMsg> PARSER =
        new com.google.protobuf.AbstractParser<PostionMsg>() {
      public PostionMsg parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PostionMsg(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<PostionMsg> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int MAP_ID_FIELD_NUMBER = 1;
    private int mapId_;
    /**
     * <code>optional int32 map_id = 1;</code>
     *
     * <pre>
     *地图真实ID
     * </pre>
     */
    public boolean hasMapId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional int32 map_id = 1;</code>
     *
     * <pre>
     *地图真实ID
     * </pre>
     */
    public int getMapId() {
      return mapId_;
    }

    public static final int MAP_KEY_FIELD_NUMBER = 2;
    private int mapKey_;
    /**
     * <code>optional int32 map_key = 2;</code>
     *
     * <pre>
     *地图模型ID
     * </pre>
     */
    public boolean hasMapKey() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional int32 map_key = 2;</code>
     *
     * <pre>
     *地图模型ID
     * </pre>
     */
    public int getMapKey() {
      return mapKey_;
    }

    public static final int POSTION_FIELD_NUMBER = 3;
    private com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 postion_;
    /**
     * <code>optional .PBVector3 postion = 3;</code>
     *
     * <pre>
     *坐标
     * </pre>
     */
    public boolean hasPostion() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional .PBVector3 postion = 3;</code>
     *
     * <pre>
     *坐标
     * </pre>
     */
    public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getPostion() {
      return postion_;
    }
    /**
     * <code>optional .PBVector3 postion = 3;</code>
     *
     * <pre>
     *坐标
     * </pre>
     */
    public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getPostionOrBuilder() {
      return postion_;
    }

    private void initFields() {
      mapId_ = 0;
      mapKey_ = 0;
      postion_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
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
        output.writeInt32(1, mapId_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt32(2, mapKey_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeMessage(3, postion_);
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
          .computeInt32Size(1, mapId_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, mapKey_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, postion_);
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

    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg prototype) {
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
     * Protobuf type {@code PostionMsg}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:PostionMsg)
        com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsgOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.app.empire.protocol.pb.warField.PostionMsgProto.internal_static_PostionMsg_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.app.empire.protocol.pb.warField.PostionMsgProto.internal_static_PostionMsg_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg.class, com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg.Builder.class);
      }

      // Construct using com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg.newBuilder()
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
          getPostionFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        mapId_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        mapKey_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        if (postionBuilder_ == null) {
          postion_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
        } else {
          postionBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.app.empire.protocol.pb.warField.PostionMsgProto.internal_static_PostionMsg_descriptor;
      }

      public com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg getDefaultInstanceForType() {
        return com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg.getDefaultInstance();
      }

      public com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg build() {
        com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg buildPartial() {
        com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg result = new com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.mapId_ = mapId_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.mapKey_ = mapKey_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        if (postionBuilder_ == null) {
          result.postion_ = postion_;
        } else {
          result.postion_ = postionBuilder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg) {
          return mergeFrom((com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg other) {
        if (other == com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg.getDefaultInstance()) return this;
        if (other.hasMapId()) {
          setMapId(other.getMapId());
        }
        if (other.hasMapKey()) {
          setMapKey(other.getMapKey());
        }
        if (other.hasPostion()) {
          mergePostion(other.getPostion());
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
        com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int mapId_ ;
      /**
       * <code>optional int32 map_id = 1;</code>
       *
       * <pre>
       *地图真实ID
       * </pre>
       */
      public boolean hasMapId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional int32 map_id = 1;</code>
       *
       * <pre>
       *地图真实ID
       * </pre>
       */
      public int getMapId() {
        return mapId_;
      }
      /**
       * <code>optional int32 map_id = 1;</code>
       *
       * <pre>
       *地图真实ID
       * </pre>
       */
      public Builder setMapId(int value) {
        bitField0_ |= 0x00000001;
        mapId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 map_id = 1;</code>
       *
       * <pre>
       *地图真实ID
       * </pre>
       */
      public Builder clearMapId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        mapId_ = 0;
        onChanged();
        return this;
      }

      private int mapKey_ ;
      /**
       * <code>optional int32 map_key = 2;</code>
       *
       * <pre>
       *地图模型ID
       * </pre>
       */
      public boolean hasMapKey() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional int32 map_key = 2;</code>
       *
       * <pre>
       *地图模型ID
       * </pre>
       */
      public int getMapKey() {
        return mapKey_;
      }
      /**
       * <code>optional int32 map_key = 2;</code>
       *
       * <pre>
       *地图模型ID
       * </pre>
       */
      public Builder setMapKey(int value) {
        bitField0_ |= 0x00000002;
        mapKey_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 map_key = 2;</code>
       *
       * <pre>
       *地图模型ID
       * </pre>
       */
      public Builder clearMapKey() {
        bitField0_ = (bitField0_ & ~0x00000002);
        mapKey_ = 0;
        onChanged();
        return this;
      }

      private com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 postion_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder> postionBuilder_;
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      public boolean hasPostion() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getPostion() {
        if (postionBuilder_ == null) {
          return postion_;
        } else {
          return postionBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      public Builder setPostion(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 value) {
        if (postionBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          postion_ = value;
          onChanged();
        } else {
          postionBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      public Builder setPostion(
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder builderForValue) {
        if (postionBuilder_ == null) {
          postion_ = builderForValue.build();
          onChanged();
        } else {
          postionBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      public Builder mergePostion(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 value) {
        if (postionBuilder_ == null) {
          if (((bitField0_ & 0x00000004) == 0x00000004) &&
              postion_ != com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance()) {
            postion_ =
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.newBuilder(postion_).mergeFrom(value).buildPartial();
          } else {
            postion_ = value;
          }
          onChanged();
        } else {
          postionBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      public Builder clearPostion() {
        if (postionBuilder_ == null) {
          postion_ = com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
          onChanged();
        } else {
          postionBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder getPostionBuilder() {
        bitField0_ |= 0x00000004;
        onChanged();
        return getPostionFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder getPostionOrBuilder() {
        if (postionBuilder_ != null) {
          return postionBuilder_.getMessageOrBuilder();
        } else {
          return postion_;
        }
      }
      /**
       * <code>optional .PBVector3 postion = 3;</code>
       *
       * <pre>
       *坐标
       * </pre>
       */
      private com.google.protobuf.SingleFieldBuilder<
          com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder> 
          getPostionFieldBuilder() {
        if (postionBuilder_ == null) {
          postionBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder>(
                  getPostion(),
                  getParentForChildren(),
                  isClean());
          postion_ = null;
        }
        return postionBuilder_;
      }

      // @@protoc_insertion_point(builder_scope:PostionMsg)
    }

    static {
      defaultInstance = new PostionMsg(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:PostionMsg)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_PostionMsg_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_PostionMsg_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\031warField/PostionMsg.proto\032\030warField/PB" +
      "Vector3.proto\"J\n\nPostionMsg\022\016\n\006map_id\030\001 " +
      "\001(\005\022\017\n\007map_key\030\002 \001(\005\022\033\n\007postion\030\003 \001(\0132\n." +
      "PBVector3B6\n#com.app.empire.protocol.pb." +
      "warFieldB\017PostionMsgProto"
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
    internal_static_PostionMsg_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_PostionMsg_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_PostionMsg_descriptor,
        new java.lang.String[] { "MapId", "MapKey", "Postion", });
    com.app.empire.protocol.pb.warField.Vector3Proto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
