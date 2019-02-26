const React = require('react');
const ReactDOM = require('react-dom');

import { Input, Icon, List, Table, Menu, Label } from 'semantic-ui-react'
import axios from "axios";
import { find, orderBy, findIndex } from 'lodash';
import cloneDeep from 'lodash/cloneDeep';

import PinRow from './PinRow'

class PinListTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = { config: {
							pins: [],
							config: []
						}
					};
		this.onPinModeChange = this.onPinModeChange.bind(this);
		this.processMessage = this.processMessage.bind(this);
		this.onPinStateChange = this.onPinStateChange.bind(this);
		
        this.webSocket = new WebSocket("ws://localhost:8080/ws");
	}

	processMessage(msg) {
		let message = JSON.parse(msg.data);
		if (message.type) {
		    if (message.type == 'SETMODESTATE') {
		        let newPinState = JSON.parse(message.jsonContext);
                const newConfig = cloneDeep(this.state.config);
		        let idx = findIndex(newConfig.config, {address: newPinState.address});
		        newConfig.config[idx] = newPinState;
                this.setState({ config: newConfig })
		    }
		}
	}
	
	componentDidMount() {
	    axios.get('/api/pins')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.config = response.data;
                this.setState(newState);
            });
		this.webSocket.onmessage = this.processMessage;

		this.webSocket.onclose = function () {
		 	alert("WebSocket connection closed") 
		};
	}
	
	
	
	onPinStateChange = (address, value) => {
	    const pinSettingsPayload = {
    	 	address: address,
			pinState: value
		};
		const message = {
			type: 'SETSTATE',
			jsonContext: JSON.stringify(pinSettingsPayload) 
		}
		this.webSocket.send( JSON.stringify(message) );
	}
	
	
	onPinModeChange = (pin, newMode) => {
    	
    	const pinSettingsPayload = {
    	 	address: pin.address,
			name: pin.name,
    		pinMode: newMode,
    		pullResistance: 'OFF'
		};
			
		const message = {
			type: 'SETMODE',
			jsonContext: JSON.stringify(pinSettingsPayload) 
		}
		
		this.webSocket.send( JSON.stringify(message) );
		
		//const url = '/api/pin/config';
		//axios.put(url, JSON.stringify(pinSettingsPayload),{
        //	headers: {
        //    	'Content-Type': 'application/json',
        //	}
    	//}).then(response => {
		//	console.log(response)
		//});

  	}

	render() {
	    let { pins } = this.state.config;
	    let { config } = this.state.config;
	    
	    if (!Array.isArray(config) || !config.length) {
	    	config = [];
	    }
	    
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
			        <Table.HeaderCell>State</Table.HeaderCell>
			      </Table.Row>
			    </Table.Header>

			    <Table.Body>
			     {pins.map(pin => (
			     	  <PinRow pin={pin} pinConfig={config} onPinStateChange={this.onPinStateChange}  onPinModeChange={this.onPinModeChange} />
			      	))}  
			    </Table.Body>
 			 </Table>)
        }
	}
}

export default (PinListTab);