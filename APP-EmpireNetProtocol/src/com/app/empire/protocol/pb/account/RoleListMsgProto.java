// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: account/RolelistMsg.proto

package com.app.empire.protocol.pb.account;

public final class RoleListMsgProto {
  private RoleListMsgProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface RoleListMsgOrBuilder extends
      // @@protoc_insertion_point(interface_extends:RoleListMsg)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    java.util.List<com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg> 
        getRoleList();
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg getRole(int index);
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    int getRoleCount();
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    java.util.List<? extends com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder> 
        getRoleOrBuilderList();
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder getRoleOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code RoleListMsg}
   */
  public static final class RoleListMsg extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:RoleListMsg)
      RoleListMsgOrBuilder {
    // Use RoleListMsg.newBuilder() to construct.
    private RoleListMsg(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private RoleListMsg(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final RoleListMsg defaultInstance;
    public static RoleListMsg getDefaultInstance() {
      return defaultInstance;
    }

    public RoleListMsg getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private RoleListMsg(
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
            case 10: {
              if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                role_ = new java.util.ArrayList<com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg>();
                mutable_bitField0_ |= 0x00000001;
              }
              role_.add(input.readMessage(com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.PARSER, extensionRegistry));
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
        if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
          role_ = java.util.Collections.unmodifiableList(role_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.app.empire.protocol.pb.account.RoleListMsgProto.internal_static_RoleListMsg_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.app.empire.protocol.pb.account.RoleListMsgProto.internal_static_RoleListMsg_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg.class, com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg.Builder.class);
    }

    public static com.google.protobuf.Parser<RoleListMsg> PARSER =
        new com.google.protobuf.AbstractParser<RoleListMsg>() {
      public RoleListMsg parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new RoleListMsg(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<RoleListMsg> getParserForType() {
      return PARSER;
    }

    public static final int ROLE_FIELD_NUMBER = 1;
    private java.util.List<com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg> role_;
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    public java.util.List<com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg> getRoleList() {
      return role_;
    }
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    public java.util.List<? extends com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder> 
        getRoleOrBuilderList() {
      return role_;
    }
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    public int getRoleCount() {
      return role_.size();
    }
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    public com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg getRole(int index) {
      return role_.get(index);
    }
    /**
     * <code>repeated .RoleMsg role = 1;</code>
     *
     * <pre>
     * 角色列表
     * </pre>
     */
    public com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder getRoleOrBuilder(
        int index) {
      return role_.get(index);
    }

    private void initFields() {
      role_ = java.util.Collections.emptyList();
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      for (int i = 0; i < getRoleCount(); i++) {
        if (!getRole(i).isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      for (int i = 0; i < role_.size(); i++) {
        output.writeMessage(1, role_.get(i));
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      for (int i = 0; i < role_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, role_.get(i));
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

    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg prototype) {
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
     * Protobuf type {@code RoleListMsg}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:RoleListMsg)
        com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsgOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.app.empire.protocol.pb.account.RoleListMsgProto.internal_static_RoleListMsg_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.app.empire.protocol.pb.account.RoleListMsgProto.internal_static_RoleListMsg_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg.class, com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg.Builder.class);
      }

      // Construct using com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg.newBuilder()
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
          getRoleFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        if (roleBuilder_ == null) {
          role_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          roleBuilder_.clear();
        }
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.app.empire.protocol.pb.account.RoleListMsgProto.internal_static_RoleListMsg_descriptor;
      }

      public com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg getDefaultInstanceForType() {
        return com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg.getDefaultInstance();
      }

      public com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg build() {
        com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg buildPartial() {
        com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg result = new com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg(this);
        int from_bitField0_ = bitField0_;
        if (roleBuilder_ == null) {
          if (((bitField0_ & 0x00000001) == 0x00000001)) {
            role_ = java.util.Collections.unmodifiableList(role_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.role_ = role_;
        } else {
          result.role_ = roleBuilder_.build();
        }
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg) {
          return mergeFrom((com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg other) {
        if (other == com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg.getDefaultInstance()) return this;
        if (roleBuilder_ == null) {
          if (!other.role_.isEmpty()) {
            if (role_.isEmpty()) {
              role_ = other.role_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureRoleIsMutable();
              role_.addAll(other.role_);
            }
            onChanged();
          }
        } else {
          if (!other.role_.isEmpty()) {
            if (roleBuilder_.isEmpty()) {
              roleBuilder_.dispose();
              roleBuilder_ = null;
              role_ = other.role_;
              bitField0_ = (bitField0_ & ~0x00000001);
              roleBuilder_ = 
                com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders ?
                   getRoleFieldBuilder() : null;
            } else {
              roleBuilder_.addAllMessages(other.role_);
            }
          }
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        for (int i = 0; i < getRoleCount(); i++) {
          if (!getRole(i).isInitialized()) {
            
            return false;
          }
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg> role_ =
        java.util.Collections.emptyList();
      private void ensureRoleIsMutable() {
        if (!((bitField0_ & 0x00000001) == 0x00000001)) {
          role_ = new java.util.ArrayList<com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg>(role_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilder<
          com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder> roleBuilder_;

      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public java.util.List<com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg> getRoleList() {
        if (roleBuilder_ == null) {
          return java.util.Collections.unmodifiableList(role_);
        } else {
          return roleBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public int getRoleCount() {
        if (roleBuilder_ == null) {
          return role_.size();
        } else {
          return roleBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg getRole(int index) {
        if (roleBuilder_ == null) {
          return role_.get(index);
        } else {
          return roleBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder setRole(
          int index, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg value) {
        if (roleBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureRoleIsMutable();
          role_.set(index, value);
          onChanged();
        } else {
          roleBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder setRole(
          int index, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder builderForValue) {
        if (roleBuilder_ == null) {
          ensureRoleIsMutable();
          role_.set(index, builderForValue.build());
          onChanged();
        } else {
          roleBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder addRole(com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg value) {
        if (roleBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureRoleIsMutable();
          role_.add(value);
          onChanged();
        } else {
          roleBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder addRole(
          int index, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg value) {
        if (roleBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureRoleIsMutable();
          role_.add(index, value);
          onChanged();
        } else {
          roleBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder addRole(
          com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder builderForValue) {
        if (roleBuilder_ == null) {
          ensureRoleIsMutable();
          role_.add(builderForValue.build());
          onChanged();
        } else {
          roleBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder addRole(
          int index, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder builderForValue) {
        if (roleBuilder_ == null) {
          ensureRoleIsMutable();
          role_.add(index, builderForValue.build());
          onChanged();
        } else {
          roleBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder addAllRole(
          java.lang.Iterable<? extends com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg> values) {
        if (roleBuilder_ == null) {
          ensureRoleIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, role_);
          onChanged();
        } else {
          roleBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder clearRole() {
        if (roleBuilder_ == null) {
          role_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          roleBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public Builder removeRole(int index) {
        if (roleBuilder_ == null) {
          ensureRoleIsMutable();
          role_.remove(index);
          onChanged();
        } else {
          roleBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder getRoleBuilder(
          int index) {
        return getRoleFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder getRoleOrBuilder(
          int index) {
        if (roleBuilder_ == null) {
          return role_.get(index);  } else {
          return roleBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public java.util.List<? extends com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder> 
           getRoleOrBuilderList() {
        if (roleBuilder_ != null) {
          return roleBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(role_);
        }
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder addRoleBuilder() {
        return getRoleFieldBuilder().addBuilder(
            com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.getDefaultInstance());
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder addRoleBuilder(
          int index) {
        return getRoleFieldBuilder().addBuilder(
            index, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.getDefaultInstance());
      }
      /**
       * <code>repeated .RoleMsg role = 1;</code>
       *
       * <pre>
       * 角色列表
       * </pre>
       */
      public java.util.List<com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder> 
           getRoleBuilderList() {
        return getRoleFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilder<
          com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder> 
          getRoleFieldBuilder() {
        if (roleBuilder_ == null) {
          roleBuilder_ = new com.google.protobuf.RepeatedFieldBuilder<
              com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg.Builder, com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsgOrBuilder>(
                  role_,
                  ((bitField0_ & 0x00000001) == 0x00000001),
                  getParentForChildren(),
                  isClean());
          role_ = null;
        }
        return roleBuilder_;
      }

      // @@protoc_insertion_point(builder_scope:RoleListMsg)
    }

    static {
      defaultInstance = new RoleListMsg(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:RoleListMsg)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoleListMsg_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_RoleListMsg_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\031account/RolelistMsg.proto\032\025account/Rol" +
      "eMsg.proto\"%\n\013RoleListMsg\022\026\n\004role\030\001 \003(\0132" +
      "\010.RoleMsgB6\n\"com.app.empire.protocol.pb." +
      "accountB\020RoleListMsgProto"
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
          com.app.empire.protocol.pb.account.RoleMsgProto.getDescriptor(),
        }, assigner);
    internal_static_RoleListMsg_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_RoleListMsg_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_RoleListMsg_descriptor,
        new java.lang.String[] { "Role", });
    com.app.empire.protocol.pb.account.RoleMsgProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}