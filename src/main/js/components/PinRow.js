import React, { Component } from "react";

import { Dropdown, Table, Input } from 'semantic-ui-react';

import { find } from 'lodash';

class PinRow extends Component {

  onPinModeChange = (e, data) => {
    let { pin } = this.props;
  	this.props.onPinModeChange(pin, data.value);
  }

  render() {
  	let { pin, pinConfig } = this.props;
  	
  	let pinModes = [];
  	pin.supportedPinModes.map(mode => {
  	 	pinModes.push({ key: mode, value: mode, text: mode });
  	})
  	
  	let defaultPinModeValue;
  	let config = find(this.props.pinConfig, { address: Number(this.props.pin.address) });
  	if (config) {
  		defaultPinModeValue = config.pinMode;
  	}
  	
  	let pullUpModes = [];
  	pin.supportedPinPullResistance.map(mode => {
  	 		pullUpModes.push({ key: mode, value: mode, text: mode });
  	})
  	let defaultPullUpMode = pullUpModes[0];
  	console.log('===============================')
  	console.log(defaultPinModeValue);
  	console.log(defaultPullUpMode);
  	console.log('===============================')
  	
  	return (
	 	<Table.Row>
        		<Table.Cell><Input value={pin.name} /></Table.Cell>
        		<Table.Cell>{pin.address}</Table.Cell>
        		<Table.Cell>
        			<Dropdown simple selection
        				placeholder='Select mode'
    					value={defaultPinModeValue}
    					onChange={this.onPinModeChange} 
    					options={pinModes} />
    			</Table.Cell>
        		<Table.Cell>
        			<Dropdown simple selection
        				 placeholder='Select pullUP'
        				 value={defaultPullUpMode}
        				 options={pullUpModes} />
        		</Table.Cell>
      	</Table.Row>  	
  		)	
  	}
  
}

export default (PinRow);