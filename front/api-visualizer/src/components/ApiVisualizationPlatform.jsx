import React, { useState, useEffect, useRef } from 'react';
import { ChevronRight, Code, Network, MessageSquare, Play, GitBranch, Shield, Clock, AlertCircle, CheckCircle, Zap } from 'lucide-react';
import * as d3 from 'd3';

const ApiVisualizationPlatform = () => {
  const [selectedApi, setSelectedApi] = useState(null);
  const [activeView, setActiveView] = useState('graph');
  const [chatMessage, setChatMessage] = useState('');
  const svgRef = useRef(null);

  // Mock API 데이터
  const apiData = {
    nodes: [
      { id: 'auth', group: 'Authentication', label: 'Auth Service', description: '인증/인가 처리' },
      { id: 'users', group: 'User Management', label: 'User API', description: '사용자 정보 관리' },
      { id: 'payment', group: 'Payment', label: 'Payment API', description: '결제 처리' },
      { id: 'orders', group: 'Order', label: 'Order API', description: '주문 관리' },
      { id: 'products', group: 'Product', label: 'Product API', description: '상품 정보' },
      { id: 'notification', group: 'Notification', label: 'Notification API', description: '알림 발송' }
    ],
    links: [
      { source: 'auth', target: 'users', type: 'validates' },
      { source: 'users', target: 'orders', type: 'creates' },
      { source: 'orders', target: 'payment', type: 'processes' },
      { source: 'orders', target: 'products', type: 'contains' },
      { source: 'payment', target: 'notification', type: 'triggers' },
      { source: 'orders', target: 'notification', type: 'notifies' }
    ]
  };

  const apiDetails = {
    users: {
      endpoints: [
        { method: 'GET', path: '/api/users', description: '사용자 목록 조회', auth: true },
        { method: 'GET', path: '/api/users/{id}', description: '사용자 상세 조회', auth: true },
        { method: 'POST', path: '/api/users', description: '사용자 생성', auth: false },
        { method: 'PUT', path: '/api/users/{id}', description: '사용자 정보 수정', auth: true },
        { method: 'DELETE', path: '/api/users/{id}', description: '사용자 삭제', auth: true }
      ],
      requestExample: `{
  "name": "홍길동",
  "email": "hong@example.com",
  "role": "USER"
}`,
      responseExample: `{
  "id": "usr_123456",
  "name": "홍길동",
  "email": "hong@example.com",
  "role": "USER",
  "createdAt": "2024-01-15T10:30:00Z"
}`,
      relatedApis: ['auth', 'orders'],
      errorCodes: [
        { code: 400, description: '잘못된 요청 형식' },
        { code: 401, description: '인증 실패' },
        { code: 404, description: '사용자를 찾을 수 없음' },
        { code: 409, description: '이미 존재하는 이메일' }
      ]
    }
  };

  // D3.js Force Graph
  useEffect(() => {
    if (activeView !== 'graph' || !svgRef.current) return;

    const width = 800;
    const height = 500;

    // Clear previous graph
    d3.select(svgRef.current).selectAll("*").remove();

    const svg = d3.select(svgRef.current)
    .attr("viewBox", [0, 0, width, height]);

    // Create force simulation
    const simulation = d3.forceSimulation(apiData.nodes)
    .force("link", d3.forceLink(apiData.links).id(d => d.id).distance(120))
    .force("charge", d3.forceManyBody().strength(-500))
    .force("center", d3.forceCenter(width / 2, height / 2));

    // Add links
    const link = svg.append("g")
    .selectAll("line")
    .data(apiData.links)
    .enter().append("line")
    .attr("stroke", "#94a3b8")
    .attr("stroke-opacity", 0.6)
    .attr("stroke-width", 2);

    // Add link labels
    const linkLabel = svg.append("g")
    .selectAll("text")
    .data(apiData.links)
    .enter().append("text")
    .attr("font-size", 10)
    .attr("fill", "#64748b")
    .text(d => d.type);

    // Add nodes
    const node = svg.append("g")
    .selectAll("g")
    .data(apiData.nodes)
    .enter().append("g")
    .style("cursor", "pointer")
    .on("click", (event, d) => setSelectedApi(d.id))
    .call(d3.drag()
    .on("start", dragstarted)
    .on("drag", dragged)
    .on("end", dragended));

    // Add circles for nodes
    node.append("circle")
    .attr("r", 30)
    .attr("fill", d => {
      const colors = {
        'Authentication': '#3b82f6',
        'User Management': '#10b981',
        'Payment': '#f59e0b',
        'Order': '#8b5cf6',
        'Product': '#ec4899',
        'Notification': '#06b6d4'
      };
      return colors[d.group] || '#6b7280';
    })
    .attr("stroke", "#fff")
    .attr("stroke-width", 2);

    // Add labels
    node.append("text")
    .attr("dy", ".35em")
    .attr("text-anchor", "middle")
    .attr("fill", "white")
    .attr("font-size", "12px")
    .attr("font-weight", "bold")
    .text(d => d.id.toUpperCase());

    // Add tooltips
    node.append("title")
    .text(d => d.description);

    // Update positions on tick
    simulation.on("tick", () => {
      link
      .attr("x1", d => d.source.x)
      .attr("y1", d => d.source.y)
      .attr("x2", d => d.target.x)
      .attr("y2", d => d.target.y);

      linkLabel
      .attr("x", d => (d.source.x + d.target.x) / 2)
      .attr("y", d => (d.source.y + d.target.y) / 2);

      node
      .attr("transform", d => `translate(${d.x},${d.y})`);
    });

    function dragstarted(event, d) {
      if (!event.active) simulation.alphaTarget(0.3).restart();
      d.fx = d.x;
      d.fy = d.y;
    }

    function dragged(event, d) {
      d.fx = event.x;
      d.fy = event.y;
    }

    function dragended(event, d) {
      if (!event.active) simulation.alphaTarget(0);
      d.fx = null;
      d.fy = null;
    }
  }, [activeView]);

  const SequenceDiagram = () => (
      <div className="bg-gray-50 p-6 rounded-lg">
        <h3 className="text-lg font-semibold mb-4 flex items-center">
          <GitBranch className="mr-2" size={20} />
          API 호출 시퀀스
        </h3>
        <div className="space-y-4">
          <div className="flex items-start space-x-4">
            <div className="w-24 text-center">
              <div className="bg-blue-500 text-white p-2 rounded">Client</div>
            </div>
            <div className="flex-1">
              <div className="h-0.5 bg-gray-300 mt-4"></div>
            </div>
            <div className="w-24 text-center">
              <div className="bg-green-500 text-white p-2 rounded">Auth API</div>
            </div>
            <div className="flex-1">
              <div className="h-0.5 bg-gray-300 mt-4"></div>
            </div>
            <div className="w-24 text-center">
              <div className="bg-purple-500 text-white p-2 rounded">User API</div>
            </div>
          </div>

          <div className="ml-12 space-y-3">
            <div className="flex items-center">
              <div className="bg-blue-100 px-3 py-1 rounded text-sm">1. Login Request</div>
              <ChevronRight className="mx-2" size={16} />
            </div>
            <div className="flex items-center ml-32">
              <div className="bg-green-100 px-3 py-1 rounded text-sm">2. Validate Credentials</div>
            </div>
            <div className="flex items-center ml-32">
              <div className="bg-green-100 px-3 py-1 rounded text-sm">3. Generate Token</div>
            </div>
            <div className="flex items-center">
              <div className="bg-blue-100 px-3 py-1 rounded text-sm">4. Token Response</div>
            </div>
            <div className="flex items-center">
              <div className="bg-blue-100 px-3 py-1 rounded text-sm">5. Get User Info</div>
              <ChevronRight className="mx-2" size={16} />
            </div>
            <div className="flex items-center ml-64">
              <div className="bg-purple-100 px-3 py-1 rounded text-sm">6. Fetch User Data</div>
            </div>
          </div>
        </div>
      </div>
  );

  const FlowChart = () => (
      <div className="bg-gray-50 p-6 rounded-lg">
        <h3 className="text-lg font-semibold mb-4 flex items-center">
          <Network className="mr-2" size={20} />
          주문 처리 플로우
        </h3>
        <div className="flex justify-between items-start">
          <div className="text-center">
            <div className="bg-blue-500 text-white p-3 rounded-lg mb-2">
              <Play size={24} />
            </div>
            <p className="text-sm">Start</p>
          </div>

          <div className="text-center">
            <div className="bg-purple-500 text-white p-3 rounded-lg mb-2">
              사용자 인증
            </div>
            <p className="text-sm text-gray-600">GET /auth/verify</p>
          </div>

          <div className="text-center">
            <div className="bg-green-500 text-white p-3 rounded-lg mb-2">
              주문 생성
            </div>
            <p className="text-sm text-gray-600">POST /orders</p>
          </div>

          <div className="text-center">
            <div className="bg-yellow-500 text-white p-3 rounded-lg mb-2">
              결제 처리
            </div>
            <p className="text-sm text-gray-600">POST /payment</p>
          </div>

          <div className="text-center">
            <div className="bg-cyan-500 text-white p-3 rounded-lg mb-2">
              알림 발송
            </div>
            <p className="text-sm text-gray-600">POST /notify</p>
          </div>

          <div className="text-center">
            <div className="bg-gray-500 text-white p-3 rounded-lg mb-2">
              <CheckCircle size={24} />
            </div>
            <p className="text-sm">Complete</p>
          </div>
        </div>

        <div className="mt-4 flex justify-center space-x-2">
          {[1, 2, 3, 4, 5].map(i => (
              <div key={i} className="w-16 h-0.5 bg-gray-400"></div>
          ))}
        </div>
      </div>
  );

  const ChatInterface = () => (
      <div className="bg-white rounded-lg shadow-lg p-4 h-96">
        <div className="flex items-center mb-4 pb-2 border-b">
          <MessageSquare className="mr-2" size={20} />
          <h3 className="text-lg font-semibold">API 문서 도우미</h3>
        </div>

        <div className="h-64 overflow-y-auto mb-4 space-y-3">
          <div className="flex justify-start">
            <div className="bg-gray-100 rounded-lg p-3 max-w-xs">
              <p className="text-sm">안녕하세요! API 문서에 대해 궁금한 점을 물어보세요.</p>
            </div>
          </div>

          {chatMessage && (
              <div className="flex justify-end">
                <div className="bg-blue-500 text-white rounded-lg p-3 max-w-xs">
                  <p className="text-sm">{chatMessage}</p>
                </div>
              </div>
          )}

          {chatMessage && (
              <div className="flex justify-start">
                <div className="bg-gray-100 rounded-lg p-3 max-w-xs">
                  <p className="text-sm">
                    User API는 사용자 정보를 관리하는 핵심 API입니다.
                    인증된 사용자만 접근 가능하며, CRUD 작업을 지원합니다.
                    주로 주문 생성 시 사용자 검증에 활용됩니다.
                  </p>
                </div>
              </div>
          )}
        </div>

        <div className="flex space-x-2">
          <input
              type="text"
              placeholder="예: User API는 언제 사용하나요?"
              className="flex-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  setChatMessage(e.target.value);
                  e.target.value = '';
                }
              }}
          />
          <button className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">
            전송
          </button>
        </div>
      </div>
  );

  return (
      <div className="min-h-screen bg-gray-100 p-4">
        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
            <h1 className="text-3xl font-bold text-gray-800 mb-2">API 문서 시각화 플랫폼</h1>
            <p className="text-gray-600">Notion API 문서를 인터랙티브하게 보세요</p>
          </div>

          {/* View Selector */}
          <div className="bg-white rounded-lg shadow-lg p-4 mb-6">
            <div className="flex space-x-4">
              <button
                  onClick={() => setActiveView('graph')}
                  className={`px-4 py-2 rounded-lg flex items-center ${
                      activeView === 'graph' ? 'bg-blue-500 text-white' : 'bg-gray-100'
                  }`}
              >
                <Network className="mr-2" size={18} />
                관계도
              </button>
              <button
                  onClick={() => setActiveView('sequence')}
                  className={`px-4 py-2 rounded-lg flex items-center ${
                      activeView === 'sequence' ? 'bg-blue-500 text-white' : 'bg-gray-100'
                  }`}
              >
                <GitBranch className="mr-2" size={18} />
                시퀀스
              </button>
              <button
                  onClick={() => setActiveView('flow')}
                  className={`px-4 py-2 rounded-lg flex items-center ${
                      activeView === 'flow' ? 'bg-blue-500 text-white' : 'bg-gray-100'
                  }`}
              >
                <Zap className="mr-2" size={18} />
                플로우
              </button>
              <button
                  onClick={() => setActiveView('chat')}
                  className={`px-4 py-2 rounded-lg flex items-center ${
                      activeView === 'chat' ? 'bg-blue-500 text-white' : 'bg-gray-100'
                  }`}
              >
                <MessageSquare className="mr-2" size={18} />
                AI 챗
              </button>
            </div>
          </div>

          {/* Main Content */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Visualization Area */}
            <div className="lg:col-span-2">
              <div className="bg-white rounded-lg shadow-lg p-6">
                {activeView === 'graph' && (
                    <div>
                      <h2 className="text-xl font-semibold mb-4 flex items-center">
                        <Network className="mr-2" size={24} />
                        API 관계도
                      </h2>
                      <svg ref={svgRef} className="w-full h-[500px]"></svg>
                      <p className="text-sm text-gray-600 mt-4">
                        💡 노드를 클릭하면 상세 정보를 볼 수 있습니다. 드래그로 위치를 조정할 수 있습니다.
                      </p>
                    </div>
                )}

                {activeView === 'sequence' && <SequenceDiagram />}
                {activeView === 'flow' && <FlowChart />}
                {activeView === 'chat' && <ChatInterface />}
              </div>
            </div>

            {/* Detail Panel */}
            <div className="space-y-6">
              {/* API Details */}
              {selectedApi && apiDetails[selectedApi] && (
                  <div className="bg-white rounded-lg shadow-lg p-6">
                    <h3 className="text-lg font-semibold mb-4 flex items-center">
                      <Code className="mr-2" size={20} />
                      {selectedApi.toUpperCase()} API 상세
                    </h3>

                    {/* Endpoints */}
                    <div className="mb-4">
                      <h4 className="font-medium mb-2">Endpoints</h4>
                      <div className="space-y-2">
                        {apiDetails[selectedApi].endpoints.map((endpoint, idx) => (
                            <div key={idx} className="flex items-center justify-between p-2 bg-gray-50 rounded">
                              <div className="flex items-center space-x-2">
                          <span className={`px-2 py-1 text-xs font-semibold rounded ${
                              endpoint.method === 'GET' ? 'bg-green-100 text-green-800' :
                                  endpoint.method === 'POST' ? 'bg-blue-100 text-blue-800' :
                                      endpoint.method === 'PUT' ? 'bg-yellow-100 text-yellow-800' :
                                          'bg-red-100 text-red-800'
                          }`}>
                            {endpoint.method}
                          </span>
                                <code className="text-sm">{endpoint.path}</code>
                              </div>
                              {endpoint.auth && <Shield className="text-gray-400" size={16} />}
                            </div>
                        ))}
                      </div>
                    </div>

                    {/* Request Example */}
                    <div className="mb-4">
                      <h4 className="font-medium mb-2">Request Example</h4>
                      <pre className="bg-gray-900 text-green-400 p-3 rounded text-xs overflow-x-auto">
                    {apiDetails[selectedApi].requestExample}
                  </pre>
                    </div>

                    {/* Error Codes */}
                    <div>
                      <h4 className="font-medium mb-2">Error Codes</h4>
                      <div className="space-y-1">
                        {apiDetails[selectedApi].errorCodes.map((error, idx) => (
                            <div key={idx} className="flex items-center space-x-2 text-sm">
                              <AlertCircle className="text-red-500" size={14} />
                              <span className="font-mono">{error.code}</span>
                              <span className="text-gray-600">{error.description}</span>
                            </div>
                        ))}
                      </div>
                    </div>
                  </div>
              )}

              {/* Learning Progress */}
              <div className="bg-white rounded-lg shadow-lg p-6">
                <h3 className="text-lg font-semibold mb-4 flex items-center">
                  <Clock className="mr-2" size={20} />
                  진행도
                </h3>
                <div className="space-y-3">
                  {Object.entries({
                    'Authentication API': 100,
                    'User API': 75,
                    'Payment API': 50,
                    'Order API': 25,
                    'Product API': 0
                  }).map(([api, progress]) => (
                      <div key={api}>
                        <div className="flex justify-between text-sm mb-1">
                          <span>{api}</span>
                          <span>{progress}%</span>
                        </div>
                        <div className="w-full bg-gray-200 rounded-full h-2">
                          <div
                              className="bg-blue-500 h-2 rounded-full transition-all duration-300"
                              style={{ width: `${progress}%` }}
                          ></div>
                        </div>
                      </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
  );
};

export default ApiVisualizationPlatform;