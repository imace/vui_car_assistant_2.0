/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\androids\\androidworkspace\\vui_car_assistant_2.0\\src\\cn\\yunzhisheng\\vui\\assistant\\media\\IMusicService.aidl
 */
package cn.yunzhisheng.vui.assistant.media;
public interface IMusicService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements cn.yunzhisheng.vui.assistant.media.IMusicService
{
private static final java.lang.String DESCRIPTOR = "cn.yunzhisheng.vui.assistant.media.IMusicService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an cn.yunzhisheng.vui.assistant.media.IMusicService interface,
 * generating a proxy if needed.
 */
public static cn.yunzhisheng.vui.assistant.media.IMusicService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof cn.yunzhisheng.vui.assistant.media.IMusicService))) {
return ((cn.yunzhisheng.vui.assistant.media.IMusicService)iin);
}
return new cn.yunzhisheng.vui.assistant.media.IMusicService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setPlayList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<cn.yunzhisheng.vui.assistant.media.TrackInfo> _arg0;
_arg0 = data.createTypedArrayList(cn.yunzhisheng.vui.assistant.media.TrackInfo.CREATOR);
this.setPlayList(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_play:
{
data.enforceInterface(DESCRIPTOR);
this.play();
reply.writeNoException();
return true;
}
case TRANSACTION_pause:
{
data.enforceInterface(DESCRIPTOR);
this.pause();
reply.writeNoException();
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_prev:
{
data.enforceInterface(DESCRIPTOR);
this.prev();
reply.writeNoException();
return true;
}
case TRANSACTION_next:
{
data.enforceInterface(DESCRIPTOR);
this.next();
reply.writeNoException();
return true;
}
case TRANSACTION_skipTo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.skipTo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isPlaying:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPlaying();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCurrentTrack:
{
data.enforceInterface(DESCRIPTOR);
cn.yunzhisheng.vui.assistant.media.TrackInfo _result = this.getCurrentTrack();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getCurrentTrackIndex:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentTrackIndex();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getPlayList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<cn.yunzhisheng.vui.assistant.media.TrackInfo> _result = this.getPlayList();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements cn.yunzhisheng.vui.assistant.media.IMusicService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void setPlayList(java.util.List<cn.yunzhisheng.vui.assistant.media.TrackInfo> playList) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(playList);
mRemote.transact(Stub.TRANSACTION_setPlayList, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void play() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_play, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void pause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void prev() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_prev, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void next() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_next, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void skipTo(int index) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
mRemote.transact(Stub.TRANSACTION_skipTo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isPlaying() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public cn.yunzhisheng.vui.assistant.media.TrackInfo getCurrentTrack() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
cn.yunzhisheng.vui.assistant.media.TrackInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentTrack, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = cn.yunzhisheng.vui.assistant.media.TrackInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getCurrentTrackIndex() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentTrackIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<cn.yunzhisheng.vui.assistant.media.TrackInfo> getPlayList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<cn.yunzhisheng.vui.assistant.media.TrackInfo> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPlayList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(cn.yunzhisheng.vui.assistant.media.TrackInfo.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_setPlayList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_play = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_pause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_prev = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_next = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_skipTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_isPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getCurrentTrack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getCurrentTrackIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getPlayList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
}
public void setPlayList(java.util.List<cn.yunzhisheng.vui.assistant.media.TrackInfo> playList) throws android.os.RemoteException;
public void play() throws android.os.RemoteException;
public void pause() throws android.os.RemoteException;
public void stop() throws android.os.RemoteException;
public void prev() throws android.os.RemoteException;
public void next() throws android.os.RemoteException;
public void skipTo(int index) throws android.os.RemoteException;
public boolean isPlaying() throws android.os.RemoteException;
public cn.yunzhisheng.vui.assistant.media.TrackInfo getCurrentTrack() throws android.os.RemoteException;
public int getCurrentTrackIndex() throws android.os.RemoteException;
public java.util.List<cn.yunzhisheng.vui.assistant.media.TrackInfo> getPlayList() throws android.os.RemoteException;
}
