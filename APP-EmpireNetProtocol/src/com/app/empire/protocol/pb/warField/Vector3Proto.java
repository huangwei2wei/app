// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: warField/PBVector3.proto

package com.app.empire.protocol.pb.warField;

public final class Vector3Proto {
  private Vector3Proto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface PBVector3OrBuilder extends
      // @@protoc_insertion_point(interface_extends:PBVector3)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional int32 X = 1;</code>
     *
     * <pre>
     *当前X
     * </pre>
     */
    boolean hasX();
    /**
     * <code>optional int32 X = 1;</code>
     *
     * <pre>
     *当前X
     * </pre>
     */
    int getX();

    /**
     * <code>optional int32 Y = 2;</code>
     *
     * <pre>
     *当前Y
     * </pre>
     */
    boolean hasY();
    /**
     * <code>optional int32 Y = 2;</code>
     *
     * <pre>
     *当前Y
     * </pre>
     */
    int getY();

    /**
     * <code>optional int32 Z = 3;</code>
     *
     * <pre>
     *当前Z
     * </pre>
     */
    boolean hasZ();
    /**
     * <code>optional int32 Z = 3;</code>
     *
     * <pre>
     *当前Z
     * </pre>
     */
    int getZ();

    /**
     * <code>optional int32 angle = 4;</code>
     *
     * <pre>
     *朝向角度
     * </pre>
     */
    boolean hasAngle();
    /**
     * <code>optional int32 angle = 4;</code>
     *
     * <pre>
     *朝向角度
     * </pre>
     */
    int getAngle();
  }
  /**
   * Protobuf type {@code PBVector3}
   */
  public static final class PBVector3 extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:PBVector3)
      PBVector3OrBuilder {
    // Use PBVector3.newBuilder() to construct.
    private PBVector3(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private PBVector3(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final PBVector3 defaultInstance;
    public static PBVector3 getDefaultInstance() {
      return defaultInstance;
    }

    public PBVector3 getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private PBVector3(
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
              x_ = input.readInt32();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              y_ = input.readInt32();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              z_ = input.readInt32();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              angle_ = input.readInt32();
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
      return com.app.empire.protocol.pb.warField.Vector3Proto.internal_static_PBVector3_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.app.empire.protocol.pb.warField.Vector3Proto.internal_static_PBVector3_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.class, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder.class);
    }

    public static com.google.protobuf.Parser<PBVector3> PARSER =
        new com.google.protobuf.AbstractParser<PBVector3>() {
      public PBVector3 parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PBVector3(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<PBVector3> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int X_FIELD_NUMBER = 1;
    private int x_;
    /**
     * <code>optional int32 X = 1;</code>
     *
     * <pre>
     *当前X
     * </pre>
     */
    public boolean hasX() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional int32 X = 1;</code>
     *
     * <pre>
     *当前X
     * </pre>
     */
    public int getX() {
      return x_;
    }

    public static final int Y_FIELD_NUMBER = 2;
    private int y_;
    /**
     * <code>optional int32 Y = 2;</code>
     *
     * <pre>
     *当前Y
     * </pre>
     */
    public boolean hasY() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional int32 Y = 2;</code>
     *
     * <pre>
     *当前Y
     * </pre>
     */
    public int getY() {
      return y_;
    }

    public static final int Z_FIELD_NUMBER = 3;
    private int z_;
    /**
     * <code>optional int32 Z = 3;</code>
     *
     * <pre>
     *当前Z
     * </pre>
     */
    public boolean hasZ() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional int32 Z = 3;</code>
     *
     * <pre>
     *当前Z
     * </pre>
     */
    public int getZ() {
      return z_;
    }

    public static final int ANGLE_FIELD_NUMBER = 4;
    private int angle_;
    /**
     * <code>optional int32 angle = 4;</code>
     *
     * <pre>
     *朝向角度
     * </pre>
     */
    public boolean hasAngle() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>optional int32 angle = 4;</code>
     *
     * <pre>
     *朝向角度
     * </pre>
     */
    public int getAngle() {
      return angle_;
    }

    private void initFields() {
      x_ = 0;
      y_ = 0;
      z_ = 0;
      angle_ = 0;
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
        output.writeInt32(1, x_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt32(2, y_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt32(3, z_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt32(4, angle_);
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
          .computeInt32Size(1, x_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, y_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, z_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, angle_);
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

    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 prototype) {
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
     * Protobuf type {@code PBVector3}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:PBVector3)
        com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3OrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.app.empire.protocol.pb.warField.Vector3Proto.internal_static_PBVector3_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.app.empire.protocol.pb.warField.Vector3Proto.internal_static_PBVector3_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.class, com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.Builder.class);
      }

      // Construct using com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.newBuilder()
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
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        x_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        y_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        z_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        angle_ = 0;
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.app.empire.protocol.pb.warField.Vector3Proto.internal_static_PBVector3_descriptor;
      }

      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 getDefaultInstanceForType() {
        return com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance();
      }

      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 build() {
        com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 buildPartial() {
        com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 result = new com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.x_ = x_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.y_ = y_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.z_ = z_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.angle_ = angle_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3) {
          return mergeFrom((com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 other) {
        if (other == com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3.getDefaultInstance()) return this;
        if (other.hasX()) {
          setX(other.getX());
        }
        if (other.hasY()) {
          setY(other.getY());
        }
        if (other.hasZ()) {
          setZ(other.getZ());
        }
        if (other.hasAngle()) {
          setAngle(other.getAngle());
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
        com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3 parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int x_ ;
      /**
       * <code>optional int32 X = 1;</code>
       *
       * <pre>
       *当前X
       * </pre>
       */
      public boolean hasX() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional int32 X = 1;</code>
       *
       * <pre>
       *当前X
       * </pre>
       */
      public int getX() {
        return x_;
      }
      /**
       * <code>optional int32 X = 1;</code>
       *
       * <pre>
       *当前X
       * </pre>
       */
      public Builder setX(int value) {
        bitField0_ |= 0x00000001;
        x_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 X = 1;</code>
       *
       * <pre>
       *当前X
       * </pre>
       */
      public Builder clearX() {
        bitField0_ = (bitField0_ & ~0x00000001);
        x_ = 0;
        onChanged();
        return this;
      }

      private int y_ ;
      /**
       * <code>optional int32 Y = 2;</code>
       *
       * <pre>
       *当前Y
       * </pre>
       */
      public boolean hasY() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional int32 Y = 2;</code>
       *
       * <pre>
       *当前Y
       * </pre>
       */
      public int getY() {
        return y_;
      }
      /**
       * <code>optional int32 Y = 2;</code>
       *
       * <pre>
       *当前Y
       * </pre>
       */
      public Builder setY(int value) {
        bitField0_ |= 0x00000002;
        y_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 Y = 2;</code>
       *
       * <pre>
       *当前Y
       * </pre>
       */
      public Builder clearY() {
        bitField0_ = (bitField0_ & ~0x00000002);
        y_ = 0;
        onChanged();
        return this;
      }

      private int z_ ;
      /**
       * <code>optional int32 Z = 3;</code>
       *
       * <pre>
       *当前Z
       * </pre>
       */
      public boolean hasZ() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional int32 Z = 3;</code>
       *
       * <pre>
       *当前Z
       * </pre>
       */
      public int getZ() {
        return z_;
      }
      /**
       * <code>optional int32 Z = 3;</code>
       *
       * <pre>
       *当前Z
       * </pre>
       */
      public Builder setZ(int value) {
        bitField0_ |= 0x00000004;
        z_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 Z = 3;</code>
       *
       * <pre>
       *当前Z
       * </pre>
       */
      public Builder clearZ() {
        bitField0_ = (bitField0_ & ~0x00000004);
        z_ = 0;
        onChanged();
        return this;
      }

      private int angle_ ;
      /**
       * <code>optional int32 angle = 4;</code>
       *
       * <pre>
       *朝向角度
       * </pre>
       */
      public boolean hasAngle() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>optional int32 angle = 4;</code>
       *
       * <pre>
       *朝向角度
       * </pre>
       */
      public int getAngle() {
        return angle_;
      }
      /**
       * <code>optional int32 angle = 4;</code>
       *
       * <pre>
       *朝向角度
       * </pre>
       */
      public Builder setAngle(int value) {
        bitField0_ |= 0x00000008;
        angle_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 angle = 4;</code>
       *
       * <pre>
       *朝向角度
       * </pre>
       */
      public Builder clearAngle() {
        bitField0_ = (bitField0_ & ~0x00000008);
        angle_ = 0;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:PBVector3)
    }

    static {
      defaultInstance = new PBVector3(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:PBVector3)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_PBVector3_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_PBVector3_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\030warField/PBVector3.proto\";\n\tPBVector3\022" +
      "\t\n\001X\030\001 \001(\005\022\t\n\001Y\030\002 \001(\005\022\t\n\001Z\030\003 \001(\005\022\r\n\005angl" +
      "e\030\004 \001(\005B3\n#com.app.empire.protocol.pb.wa" +
      "rFieldB\014Vector3Proto"
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
        }, assigner);
    internal_static_PBVector3_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_PBVector3_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_PBVector3_descriptor,
        new java.lang.String[] { "X", "Y", "Z", "Angle", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
