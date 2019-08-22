import __ from 'lodash'
import React, { Component } from 'react'
import BluetoothAdapter from './BluetoothAdapter'
import { List, ListItem, Text } from 'native-base'

export default class MyListComponent extends Component {
    constructor(props) {
        super(props)
    }

    startBluetoothService (address) {
        BluetoothAdapter.bluetoothServerInitial(address, '00001101-0000-1000-8000-00805f9b34fb', (err) => {
          console.log('err ', err)
        }, () => {
          BluetoothAdapter.bluetoothServerStart((err) => {
            console.log('err ', err)
          }, () => {
            console.log('start server success')
          })
        })
    }

    render() {
        let { devices } = this.props
        if (devices.length) {
            return (
                <List>
                    {
                        __.map(devices, item => (
                            <ListItem key={item.key} onPress={() => {
                                this.startBluetoothService(item.address)
                            }}>
                                <Text>{item.name}</Text>
                            </ListItem>
                        ))
                    }
                </List>
            )
        } else {
            return (
                <Text>{'Không có thiết bị'}</Text>
            )
        }
    }
}