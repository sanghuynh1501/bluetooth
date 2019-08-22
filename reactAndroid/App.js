/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */
import __ from 'lodash'
import moment from 'moment'
import React, {Fragment} from 'react'
import { BleManager } from 'react-native-ble-plx'
import { PermissionsAndroid, DeviceEventEmitter } from 'react-native'

import Wav from './Wav'
import BluetoothAdapter from './BluetoothAdapter'
import DeviceList from './DeviceList'

import { Container, Header, Content, List, ListItem, Text } from 'native-base';

// import { decode  } from 'base-64'
// const Buffer = require('buffer/').Buffer
// let SERVICE_UUID           = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
// let CHARACTERISTIC_NOTIFY = "beb5483e-36e1-4688-b7f5-ea07361b26a9"
// let CHARACTERISTIC_UUID_TX = "beb5483e-36e1-4688-b7f5-ea07361b26a8"

class App extends React.Component {
  constructor (props) {
    super(props)
    this.writed = false
    this.state = {
      isEnabled: false,
      discovering: false,
      devices: [],
      unpairedDevices: [],
      connected: false,
      section: 0
    }
  }

  componentDidMount () {
    this.subscription = DeviceEventEmitter.addListener('deviceInfo', e => {
      console.log('e ', e)
      this.setState(prevState => {
        let index = __.findIndex(prevState.devices, item => item.key === e.address)
        if (index < 0) {
          prevState.devices.push({
            key: e.address,
            name: e.name,
            address: e.address
          })
        } 
        return prevState
      })
    });
    this.getBluetoothAdapter()
    this.scanBluetoothAdapter()
  }

  async getBluetoothAdapter () {
    try {
      await BluetoothAdapter.getBluetoothAdapter()
    } catch(err) {
      console.log('err ', err)
    }
  }

  async scanBluetoothAdapter () {
    BluetoothAdapter.bluetoothServerScan((err) => {
      console.log('err ', err)
    }, () => {

    })
  }

  // async connectBluetooth () {
  //   try {
  //     const granted = await PermissionsAndroid.request(
  //       PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION,
  //       {
  //         title: 'Cool Photo App Camera Permission',
  //         message:
  //           'Cool Photo App needs access to your camera ' +
  //           'so you can take awesome pictures.',
  //         buttonNeutral: 'Ask Me Later',
  //         buttonNegative: 'Cancel',
  //         buttonPositive: 'OK',
  //       }
  //     )
  //     if (granted === PermissionsAndroid.RESULTS.GRANTED) {
  //       const subscription = this.manager.onStateChange((state) => {
  //         console.log('state ', state)
  //         this.scanAndConnect()
  //         // subscription.remove()
  //       }, true)
  //     } else {
  //       console.log('Camera permission denied');
  //     }
  //   } catch (err) {
  //     console.warn(err);
  //   }
  // }

  // scanAndConnect() {
  //   this.manager.startDeviceScan(null, null, (error, device) => {
  //     if (error) {
  //       console.log('error ', error)
  //       return
  //     }
  //     console.log('device.name ', device.name)
  //     if (device.name === 'ESP32') {
  //       this.manager.stopDeviceScan()
  //       startBluetoothServer(device.id)
  //     }
  //   })
  // }

  render () {
    let { devices } = this.state
    return (
      <Container>
        <Header />
        <Content>
          <DeviceList devices={devices} />
          {/* <ScrollView
            contentInsetAdjustmentBehavior="automatic"
            style={styles.scrollView}>
            <Header />
            <View>
              <Button
                title='Connect BLE'
                onPress = {async () => {
                  this.startBluetoothServer()
                }} 
              />
            </View>
          </ScrollView> */}
        </Content>
      </Container>
    )
  }
}

export default App;
