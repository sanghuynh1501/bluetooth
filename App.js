/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */
import React, {Fragment} from 'react'
import moment from 'moment'
import { BleManager } from 'react-native-ble-plx'
import { PermissionsAndroid } from 'react-native'
import Wav from './Wav'
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  Button,
  StatusBar,
} from 'react-native';

import {
  Header,
  Colors,
} from 'react-native/Libraries/NewAppScreen'

import { decode  } from 'base-64'
const Buffer = require('buffer/').Buffer
let SERVICE_UUID           = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
let CHARACTERISTIC_NOTIFY = "beb5483e-36e1-4688-b7f5-ea07361b26a9"
let CHARACTERISTIC_UUID_TX = "beb5483e-36e1-4688-b7f5-ea07361b26a8"

class App extends React.Component {
  constructor (props) {
    super(props)
    this.manager = new BleManager();
    this.writed = false
  }

  async connectBLE () {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION,
        {
          title: 'Cool Photo App Camera Permission',
          message:
            'Cool Photo App needs access to your camera ' +
            'so you can take awesome pictures.',
          buttonNeutral: 'Ask Me Later',
          buttonNegative: 'Cancel',
          buttonPositive: 'OK',
        }
      )
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        const subscription = this.manager.onStateChange((state) => {
          console.log('state ', state)
          this.scanAndConnect()
          // subscription.remove()
        }, true)
      } else {
        console.log('Camera permission denied');
      }
    } catch (err) {
      console.warn(err);
    }
  }

  scanAndConnect() {
    this.manager.startDeviceScan(null, null, (error, device) => {
      if (error) {
        console.log('error ', error)
          // Handle error (scanning will be stopped automatically)
        return
      }

      console.log('device.name ', device.name)

      // Check if it is a device you are looking for based on advertisement data
      // or other criteria.
      if (device.name === 'ESP32') {
        let that = this
        // Stop scanning as it's not necessary if you are scanning for one device.
        this.manager.stopDeviceScan();
        
        // Proceed with connection.
        this.manager.connectToDevice(
          device.id
        ).then(() => {
          let audioString = []
          device.discoverAllServicesAndCharacteristics(device.id).then(() => {
            // setInterval(() => {
            let start = moment().valueOf()
            device.monitorCharacteristicForService(
              SERVICE_UUID,
              CHARACTERISTIC_NOTIFY,
              (err, notify) => {
                if (notify.value) {
                  if (decode(notify.value) !== 'END') {
                    let hexBuffer = Buffer.from(Uint8Array.from(decode(notify.value), c => c.charCodeAt(0))).toString('hex')
                    console.log('buffer ', hexBuffer)
                    if (!that.writed) {
                      audioString.push(hexBuffer)
                    }
                    // device.readCharacteristicForService(SERVICE_UUID, CHARACTERISTIC_UUID_TX).then(characteristic => {
                    //   let hexBuffer = Buffer.from(Uint8Array.from(decode(characteristic.value), c => c.charCodeAt(0))).toString('hex')
                    //   console.log('hexBuffer ', hexBuffer)
                    //   if (characteristic.value && !that.writed) {
                    //     audioString.push(hexBuffer)
                    //   }
                    // }).catch((err) => {
                    //   console.log('err ', err)
                    // })
                  } else {
                    console.log('value ', decode(notify.value))
                    if (!that.writed) {
                      Wav.writeWav(audioString.toString())
                      let end = moment().valueOf()
                      let time = end - start
                      console.log('time ', time / 1000)
                      that.writed = true
                    }
                    // device.cancelConnection()
                  }
                }
              }
            )
            // }, 20)
            // that.interval = setInterval(() => {
            //   device.readCharacteristicForService(SERVICE_UUID, CHARACTERISTIC_NOTIFY).then(characteristic => {
            //     console.log('value ', decode(characteristic.value))
            //   //   let hexBuffer = Buffer.from(Uint8Array.from(decode(characteristic.value), c => c.charCodeAt(0))).toString('hex')
            //   //   console.log('hexBuffer ', hexBuffer)
            //   //   if (hexBuffer.indexOf('524946463280010057415645666d74201200000003005d1ed03f7ce') > 0) {
            //   //     that.writed = false
            //   //   }
            //   //   if (characteristic.value && !that.writed) {
            //   //     audioString.push(hexBuffer)
            //   //     Wav.writeWav(audioString.toString())
            //   //     if (hexBuffer.indexOf('a23f 6c3f 7f41 d990 a13f') > 0) {
            //   //       that.writed = true
            //   //       device.cancelConnection()
            //   //     }
            //   //   }
            //   }).catch((err) => {
            //     console.log('err ', err)
            //   })
            // }, 20)
          }).catch((err) => {
            console.log('err ', err)
          })
        })
      }
    })
  }

  render () {
    return (
      <Fragment>
        <StatusBar barStyle="dark-content" />
        <SafeAreaView>
          <ScrollView
            contentInsetAdjustmentBehavior="automatic"
            style={styles.scrollView}>
            <Header />
            <View>
              <Button
                title='Connect BLE'
                onPress = {async () => {
                  // try {
                  //   const grantedRead = await PermissionsAndroid.request(
                  //     PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
                  //     {
                  //       title: 'Cool Photo App Camera Permission',
                  //       message:
                  //         'Cool Photo App needs access to your camera ' +
                  //         'so you can take awesome pictures.',
                  //       buttonNeutral: 'Ask Me Later',
                  //       buttonNegative: 'Cancel',
                  //       buttonPositive: 'OK',
                  //     }
                  //   )
                  //   const grantedWrite = await PermissionsAndroid.request(
                  //     PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
                  //     {
                  //       title: 'Cool Photo App Camera Permission',
                  //       message:
                  //         'Cool Photo App needs access to your camera ' +
                  //         'so you can take awesome pictures.',
                  //       buttonNeutral: 'Ask Me Later',
                  //       buttonNegative: 'Cancel',
                  //       buttonPositive: 'OK',
                  //     }
                  //   )
                  //   if (grantedRead && grantedWrite) {
                  //     Wav.writeWav()
                  //   } else {
                  //     console.log('Camera permission denied');
                  //   }
                  // } catch (err) {
                  //   console.warn(err);
                  // }
                  // WaveProcess.writeWav()
                  this.connectBLE()
                }} 
              />                
            </View>
          </ScrollView>
        </SafeAreaView>
      </Fragment>
    )
  }
}

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
