const React = require('react');
const ReactDOM = require('react-dom');
import { Input, Icon, List, Table, Menu, Label } from 'semantic-ui-react'


import axios from "axios";
import { find } from 'lodash';

import PinModesList from './PinModesList'
import PullUpModeList from './PullUpModeList'

class PinListTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = {pins: {}};
	}
	
	componentDidMount() {
	    axios.get('/api/pins')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.pins = response.data;
                this.setState(newState);
            });
	}
	
	onPinModeChange = (pin, newMode) => {
	    console.log('--------------------------');
    	console.log(pin);
    	console.log(newMode);
    	console.log('--------------------------');
    	
    	//const pin = find(this.state.pins, { address: Number(id) });
    	
    	const pinSettingsPayload = {
    	 	address: pin.address,
			name: pin.name,
    		pinMode: newMode,
    		pullResistance: 'OFF'
		};
		const url = '/api/pin/config';
		axios.put(url, JSON.stringify(pinSettingsPayload),{
        	headers: {
            	'Content-Type': 'application/json',
        	}
    	}).then(response => {
			console.log(response)
		});

  	}

	render() {
	    let { pins } = this.state;
	    
	    if (!Array.isArray(pins) || !pins.length) {
            return  <Label>Loading...</Label>
        } else {
            return (
             <Table celled>
			    <Table.Header>
			      <Table.Row>
			        <Table.HeaderCell>Name</Table.HeaderCell>
			        <Table.HeaderCell>Address</Table.HeaderCell>
			        <Table.HeaderCell>Mode</Table.HeaderCell>
			        <Table.HeaderCell>PullUP</Table.HeaderCell>
			      </Table.Row>
			    </Table.Header>

			    <Table.Body>
			     {pins.map(pin => (
			          <Table.Row>
			        		<Table.Cell><Input value={pin.name} /></Table.Cell>
			        		<Table.Cell>{pin.address}</Table.Cell>
			        		<Table.Cell><PinModesList onPinModeChange={this.onPinModeChange} pin={pin}/></Table.Cell>
			        		<Table.Cell><PullUpModeList modes={pin.supportedPinPullResistance}/></Table.Cell>
			      	</Table.Row>
			      	))}  
			    </Table.Body>
 			 </Table>)
        }
	}
}

export default (PinListTab);